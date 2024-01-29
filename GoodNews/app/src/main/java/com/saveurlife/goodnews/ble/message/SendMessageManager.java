package com.saveurlife.goodnews.ble.message;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.saveurlife.goodnews.ble.BleMeshConnectedUser;
import com.saveurlife.goodnews.main.PreferencesUtil;
import com.saveurlife.goodnews.service.LocationService;
import com.saveurlife.goodnews.service.UserDeviceInfoService;

import java.text.SimpleDateFormat;
import java.util.*;

public class SendMessageManager {
    private static SendMessageManager instance;

    private PreferencesUtil preferencesUtil;
    private UUID serviceUUID;
    private UUID characteristicUUID;
    private UserDeviceInfoService userDeviceInfoService;
    private LocationService locationService;
    private String myId;
    private String myName;

    private int initUserSize = 1;

    public static SendMessageManager getInstance(UUID serviceUUID, UUID characteristicUUID,
                                                 UserDeviceInfoService userDeviceInfoService, LocationService locationService, PreferencesUtil preferencesUtil, String myName){
        if(instance==null){
            instance=new SendMessageManager(serviceUUID, characteristicUUID, userDeviceInfoService, locationService, preferencesUtil, myName);
        }
        return instance;
    }


   private SendMessageManager(UUID serviceUUID, UUID characteristicUUID,
                              UserDeviceInfoService userDeviceInfoService, LocationService locationService, PreferencesUtil preferencesUtil, String myName) {
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.userDeviceInfoService = userDeviceInfoService;
        this.locationService = locationService;
        this.myId = userDeviceInfoService.getDeviceId();
        this.preferencesUtil = preferencesUtil; // preferencesUtil 값 설정
        this.myName = myName;
    }


    public String[] getOpenLocation1(){
        if(preferencesUtil.getBoolean("myLocation", false)){
            String[] location = locationService.getLastKnownLocation().split("/");
            return location;
        }
        else{
            String[] location=new String[]{"0.0","0.0"};
            return location;
        }
    }

    public String getOpenLocation2(){
        if(preferencesUtil.getBoolean("myLocation", false)){
            String[] location = locationService.getLastKnownLocation().split("/");
            return location[0]+"/"+location[1];
        }
        else{
            return "0.0/0.0";
        }
    }

    static class Message{
       String content;
       BluetoothGatt targetGatt;
       public Message(String content, BluetoothGatt targetGatt){
           this.content=content;
           this.targetGatt=targetGatt;
       }
    }

    private Queue<Message> messageQueue = new ArrayDeque<>();

    public void createInitMessage(BluetoothGatt deviceGatt, Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap){
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String formattedDate = sdf.format(now);

        Map<String, BleMeshConnectedUser> newBleMeshConnectedDevicesMap = new HashMap<>();

        String[] location = getOpenLocation1();

        BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(myId, myName, formattedDate, preferencesUtil.getString("status", "4"), Double.parseDouble(location[0]), Double.parseDouble(location[1]));

        newBleMeshConnectedDevicesMap.put(myId, bleMeshConnectedUser);
        for (Map<String, BleMeshConnectedUser> part : bleMeshConnectedDevicesMap.values()) {
            newBleMeshConnectedDevicesMap.putAll(part);
        }

        int maxSize = newBleMeshConnectedDevicesMap.size() % initUserSize == 0 ? newBleMeshConnectedDevicesMap.size() / initUserSize : newBleMeshConnectedDevicesMap.size() / initUserSize + 1;
        int nowSize = 1;

        String content = "init/" + myId + "/" + maxSize + "/" + nowSize + "/";

        int count = 0;

        for (BleMeshConnectedUser user : newBleMeshConnectedDevicesMap.values()) {
            content += user.toInitString();
            count++;
            if (count != 0 && (count % initUserSize == 0 || count == newBleMeshConnectedDevicesMap.size())) {
                addMessageToMessageQueue(new Message(content, deviceGatt));
                nowSize++;
                content = "init/" + myId + "/" + maxSize + "/" + nowSize + "/";
            } else {
                content += "@";
            }
        }
    }

    public void createSpreadMessage(Map<String, BluetoothGatt> deviceGattMap, String content){
        for (BluetoothGatt gatt : deviceGattMap.values()) {
            addMessageToMessageQueue(new Message(content, gatt));
        }
    }

    public void createBaseMessage(Map<String, BluetoothGatt> deviceGattMap) {
        Log.i("연결 기기 수 : ", Integer.toString(deviceGattMap.size()));
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String formattedDate = sdf.format(now);

        MessageBase messageBase = new MessageBase();
        messageBase.setMessageType("base");
        messageBase.setSenderId(myId);
        messageBase.setSenderName(myName);
        messageBase.setSendTime(formattedDate);
        messageBase.setHealthStatus(preferencesUtil.getString("status", "4"));
        messageBase.setLocation(getOpenLocation2());
        String content = messageBase.toString();

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            addMessageToMessageQueue(new Message(content,gatt));
        }
    }

    public void createHelpMessage(Map<String, BluetoothGatt> deviceGattMap) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String formattedDate = sdf.format(now);

        MessageHelp messageHelp = new MessageHelp();
        messageHelp.setMessageType("help");
        messageHelp.setSenderId(myId);
        messageHelp.setSenderName(myName);
        messageHelp.setSendTime(formattedDate);
        messageHelp.setHealthStatus(preferencesUtil.getString("status", "4"));
        messageHelp.setLocation(getOpenLocation2());

        String content = messageHelp.toString();

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            addMessageToMessageQueue(new Message(content,gatt));
        }
    }

    public void createChatMessage(Map<String, BluetoothGatt> deviceGattMap, String receiverId, String receiverName, String content) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");

        String formattedDate = sdf.format(now);

        MessageChat messageChat = new MessageChat();
        messageChat.setMessageType("chat");
        messageChat.setSenderId(myId);
        messageChat.setSenderName(myName);
        messageChat.setSendTime(formattedDate);
        messageChat.setHealthStatus(preferencesUtil.getString("status", "4"));
        messageChat.setLocation(getOpenLocation2());
        messageChat.setReceiverId(receiverId);
        messageChat.setReceiverName(receiverName);
        messageChat.setContent(content);

        String message = messageChat.toString();

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            addMessageToMessageQueue(new Message(message, gatt));
        }
    }

    public void createGroupInviteMessage(Map<String, BluetoothGatt> deviceGattMap, List<String> receiverIds, String groupId, String groupName){
        for(int i=1;i<=5;i++){
            receiverIds.add("member"+i);
        }
        receiverIds.add("5108c2a83bbf0253");
        receiverIds.add("4c311a7721a68c6e");


        String content = "invite/"+myId+"/";
        receiverIds.add(myId);


        for(int i=1; i<=receiverIds.size(); i++){
            content = content.concat(receiverIds.get(i-1));
            content = content.concat("@");
        }

        content=content.substring(0,content.length()-1);
        content = content.concat("/" + groupId);
        content = content.concat("/" + groupName);

        Log.i("invite", content);

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            addMessageToMessageQueue(new Message(content, gatt));
        }

        content = "invite/"+myId+"/";
    }


//    public void createGroupInviteMessage(Map<String, BluetoothGatt> deviceGattMap, List<String> receiverIds, String groupId, String groupName){
//        String content = "invite/"+myId+"/";
//        receiverIds.add(myId);
//        for(int i=0; i<receiverIds.size(); i++){
//            content = content.concat(receiverIds.get(i));
//            if(i<receiverIds.size()-1){
//                content = content.concat("@");
//            }
//        }
//
//        content = content.concat("/" + groupId);
//        content = content.concat("/" + groupName);
//
//        Log.i("invite", content);
//
//        for (BluetoothGatt gatt : deviceGattMap.values()) {
//            addMessageToMessageQueue(new Message(content, gatt));
//        }
//    }

    public void createDisconnectMessage(BluetoothGatt gatt) {
        String content="disconnect/"+myId;
        addMessageToMessageQueue(new Message(content, gatt));
    }

    public void createChangeMessage(Map<String, BluetoothGatt> deviceGattMap, Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap){
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String formattedDate = sdf.format(now);
        String[] location = getOpenLocation1();

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            // 여기서 자기 꺼 빼고 보내게
            Map<String, BleMeshConnectedUser> newBleMeshConnectedDevicesMap=new HashMap<>();
            BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(myId, myName, formattedDate, preferencesUtil.getString("status", "4"), Double.parseDouble(location[0]), Double.parseDouble(location[1]));
            newBleMeshConnectedDevicesMap.put(myId, bleMeshConnectedUser);

            for(Map<String, BleMeshConnectedUser> part : bleMeshConnectedDevicesMap.values()){
                newBleMeshConnectedDevicesMap.putAll(part);
            }

            int count = 0;
            int maxSize = newBleMeshConnectedDevicesMap.size()% initUserSize ==0 ? newBleMeshConnectedDevicesMap.size()/ initUserSize :  newBleMeshConnectedDevicesMap.size()/ initUserSize +1;
            int nowSize = 1;

            String content="change/"+myId+"/"+maxSize+"/"+nowSize+"/";

            for(BleMeshConnectedUser user : newBleMeshConnectedDevicesMap.values()){
                content+=user.toInitString();
                count++;

                if(count!=0&&(count% initUserSize ==0||count==newBleMeshConnectedDevicesMap.size())){
                    addMessageToMessageQueue(new Message(content, gatt));
                    nowSize++;
                    content="change/"+myId+"/"+maxSize+"/"+nowSize+"/";
                    continue;
                }
                content+="@";
            }
        }
    }

    public void createDangerInfoMessage(Map<String, BluetoothGatt> deviceGattMap, String dangerInfo){
        String content="danger/"+myId+"/"+dangerInfo;
        for (BluetoothGatt gatt : deviceGattMap.values()) {
            addMessageToMessageQueue(new Message(content,gatt));
        }
    }


    public void addMessageToMessageQueue(Message message){
        messageQueue.offer(message);
        if(!sending){
            sendNextMessageQueue();
        }
    }

    private boolean sending=false;

    public void setSendingTrue(){
        sending=true;
    }
    public void setSendingFalse(){
        sending=false;
    }

    public void sendNextMessageQueue(){
        if(!messageQueue.isEmpty()){
            setSendingTrue();
            Message nowMessage=messageQueue.poll();

            BluetoothGattService service = nowMessage.targetGatt.getService(serviceUUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                if (characteristic != null) {
                    characteristic.setValue(nowMessage.content);
                    nowMessage.targetGatt.writeCharacteristic(characteristic);
                }
                else{
                    setSendingFalse();
                }
            }
            else{
                setSendingFalse();
            }
        }
    }
}
