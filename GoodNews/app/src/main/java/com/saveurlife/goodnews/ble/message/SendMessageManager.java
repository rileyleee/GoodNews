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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.*;
public class SendMessageManager {
    private PreferencesUtil preferencesUtil;
    private UUID serviceUUID;
    private UUID characteristicUUID;
    private UserDeviceInfoService userDeviceInfoService;
    private LocationService locationService;
    private String myId;
    private String myName;

    private int sendSize = 3;

   public SendMessageManager(UUID serviceUUID, UUID characteristicUUID,
                              UserDeviceInfoService userDeviceInfoService, LocationService locationService, PreferencesUtil preferencesUtil, String myName) {
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.userDeviceInfoService = userDeviceInfoService;
        this.locationService = locationService;
        this.myId = userDeviceInfoService.getDeviceId();
        this.preferencesUtil = preferencesUtil; // preferencesUtil 값 설정
        this.myName = myName;
    }


    private Queue<String> initMessageQueue = new LinkedList<>();
    public void prepareInitMessage(BluetoothGatt deviceGatt, Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap){
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String formattedDate = sdf.format(now);

        Map<String, BleMeshConnectedUser> newBleMeshConnectedDevicesMap = new HashMap<>();

        String[] location = locationService.getLastKnownLocation().split("/");

        BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(myId, myName, formattedDate, preferencesUtil.getString("status", "4"), Double.parseDouble(location[0]), Double.parseDouble(location[1]));

        newBleMeshConnectedDevicesMap.put(myId, bleMeshConnectedUser);
        for (Map<String, BleMeshConnectedUser> part : bleMeshConnectedDevicesMap.values()) {
            newBleMeshConnectedDevicesMap.putAll(part);
        }

        int maxSize = newBleMeshConnectedDevicesMap.size() % sendSize == 0 ? newBleMeshConnectedDevicesMap.size() / sendSize : newBleMeshConnectedDevicesMap.size() / sendSize + 1;
        int nowSize = 1;

        String result = "init/" + myId + "/" + maxSize + "/" + nowSize + "/";

        int count = 0;

        Log.i("init 유저 수", Integer.toString(newBleMeshConnectedDevicesMap.size()));
        for (BleMeshConnectedUser user : newBleMeshConnectedDevicesMap.values()) {
            result += user.toInitString();
            count++;
            if (count != 0 && (count % sendSize == 0 || count == newBleMeshConnectedDevicesMap.size())) {
                initMessageQueue.add(result);

                nowSize++;
                result = "init/" + myId + "/" + maxSize + "/" + nowSize + "/";
            } else {
                result += "@";
            }
        }
        Log.i("메시지큐 사이즈", Integer.toString(initMessageQueue.size()));
        Log.i("메시지큐", initMessageQueue.toString());

//        sendMessageInit(deviceGatt,bleMeshConnectedDevicesMap);

        sendInitMessageQueue(deviceGatt);

    }

    public void sendInitMessageQueue(BluetoothGatt deviceGatt) {
        BluetoothGattService service = deviceGatt.getService(serviceUUID);

        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic != null) {
                if(!initMessageQueue.isEmpty()){
                    String message = initMessageQueue.poll();
//                    Log.i("init message", message);
                    characteristic.setValue(message);
                    deviceGatt.writeCharacteristic(characteristic);
                }
            }
            else{
                initMessageQueue.clear();
            }
        }
        else{
            initMessageQueue.clear();
        }
    }



    public String sendMessageInit(BluetoothGatt deviceGatt, Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String formattedDate = sdf.format(now);

        Map<String, BleMeshConnectedUser> newBleMeshConnectedDevicesMap = new HashMap<>();

        String[] location = locationService.getLastKnownLocation().split("/");

        BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(myId, myName, formattedDate, preferencesUtil.getString("status", "4"), Double.parseDouble(location[0]), Double.parseDouble(location[1]));

        newBleMeshConnectedDevicesMap.put(myId, bleMeshConnectedUser);
        for (Map<String, BleMeshConnectedUser> part : bleMeshConnectedDevicesMap.values()) {
            newBleMeshConnectedDevicesMap.putAll(part);
        }

        int maxSize = newBleMeshConnectedDevicesMap.size() % sendSize == 0 ? newBleMeshConnectedDevicesMap.size() / sendSize : newBleMeshConnectedDevicesMap.size() / sendSize + 1;
        int nowSize = 1;

        String result = "init/" + myId + "/" + maxSize + "/" + nowSize + "/";
        BluetoothGattService service = deviceGatt.getService(serviceUUID);

        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic != null) {
                int count = 0;

                for (BleMeshConnectedUser user : newBleMeshConnectedDevicesMap.values()) {
                    result += user.toInitString();
                    count++;

                    if (count != 0 && (count % sendSize == 0 || count == newBleMeshConnectedDevicesMap.size())) {
                        characteristic.setValue(result);
                        deviceGatt.writeCharacteristic(characteristic);

                        // 여기에서 writeCharacteristic()가 완료될 때까지 대기하거나 콜백을 사용해야 합니다.
                        // 쓰기 작업이 완료되면 다음 쓰기 작업을 시작해야 합니다.

                        nowSize++;
                        result = "init/" + myId + "/" + maxSize + "/" + nowSize + "/";
                    } else {
                        result += "@";
                    }
                }
            }
        }
        return result;
    }


    public void spreadMessage(Map<String, BluetoothGatt> deviceGattMap, String content){
        for (BluetoothGatt gatt : deviceGattMap.values()) {
            BluetoothGattService service = gatt.getService(serviceUUID);

            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                if (characteristic != null) {
                    characteristic.setValue(content);
                    gatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    public String sendMessageBase(Map<String, BluetoothGatt> deviceGattMap) {
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
        messageBase.setLocation(locationService.getLastKnownLocation());

        String message = messageBase.toString();

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            BluetoothGattService service = gatt.getService(serviceUUID);

            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                if (characteristic != null) {
                    characteristic.setValue(message);
                    gatt.writeCharacteristic(characteristic);
                }
            }
        }
        return message;
    }

    public void sendMessageHelp(Map<String, BluetoothGatt> deviceGattMap) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String formattedDate = sdf.format(now);

        MessageHelp messageHelp = new MessageHelp();
        messageHelp.setMessageType("help");
        messageHelp.setSenderId(myId);
        messageHelp.setSenderName(myName);
        messageHelp.setSendTime(formattedDate);
        messageHelp.setHealthStatus(preferencesUtil.getString("status", "4"));
        messageHelp.setLocation(locationService.getLastKnownLocation());

        String message = messageHelp.toString();

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            BluetoothGattService service = gatt.getService(serviceUUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                if (characteristic != null) {
                    characteristic.setValue(message);
                    gatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    public String sendMessageChat(Map<String, BluetoothGatt> deviceGattMap, String receiverId, String receiverName, String content) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
//        SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm", Locale.getDefault());

        String formattedDate = sdf.format(now);

        MessageChat messageChat = new MessageChat();
        messageChat.setMessageType("chat");
        messageChat.setSenderId(myId);
        messageChat.setSenderName(myName);
        messageChat.setSendTime(formattedDate);
        messageChat.setHealthStatus(preferencesUtil.getString("status", "4"));
        messageChat.setLocation(locationService.getLastKnownLocation());
        messageChat.setReceiverId(receiverId);
        messageChat.setReceiverName(receiverName);
        messageChat.setContent(content);

        String message = messageChat.toString();

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            BluetoothGattService service = gatt.getService(serviceUUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                if (characteristic != null) {
                    characteristic.setValue(message);
                    gatt.writeCharacteristic(characteristic);
                }
            }
        }
        return message;
    }

    public void sendMessageGroupInvite(Map<String, BluetoothGatt> deviceGattMap, List<String> receiverIds, String groupId, String groupName){
        String message = "invite/"+myId+"/";
        receiverIds.add(myId);
        for(int i=0; i<receiverIds.size(); i++){
            message = message.concat(receiverIds.get(i));
            if(i<receiverIds.size()-1){
                message = message.concat("@");
            }
        }

        message = message.concat("/" + groupId);
        message = message.concat("/" + groupName);

        Log.i("invite", message);

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            BluetoothGattService service = gatt.getService(serviceUUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                if (characteristic != null) {
                    characteristic.setValue(message);
                    gatt.writeCharacteristic(characteristic);
                }
            }
        }
    }


    public void sendMessageDisconnect(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic != null) {
                characteristic.setValue("disconnect/"+myId);
                gatt.writeCharacteristic(characteristic);
            }
        }
    }


    // 직접 연결되거나 직접 연결된 디바이스가 끊어질 때만
    public void sendMessageChange(Map<String, BluetoothGatt> deviceGattMap, Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap){
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String formattedDate = sdf.format(now);
        String[] location = locationService.getLastKnownLocation().split("/");

        for (BluetoothGatt gatt : deviceGattMap.values()) {
            // 여기서 자기 꺼 빼고 보내게
            Map<String, BleMeshConnectedUser> newBleMeshConnectedDevicesMap=new HashMap<>();
            BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(myId, myName, formattedDate, preferencesUtil.getString("status", "4"), Double.parseDouble(location[0]), Double.parseDouble(location[1]));
            newBleMeshConnectedDevicesMap.put(myId, bleMeshConnectedUser);

            for(Map<String, BleMeshConnectedUser> part : bleMeshConnectedDevicesMap.values()){
                newBleMeshConnectedDevicesMap.putAll(part);
            }

            int count = 0;
            int maxSize = newBleMeshConnectedDevicesMap.size()%sendSize==0 ? newBleMeshConnectedDevicesMap.size()/sendSize :  newBleMeshConnectedDevicesMap.size()/sendSize+1;
            int nowSize = 1;

            String message="change/"+myId+"/"+maxSize+"/"+nowSize+"/";

            BluetoothGattService service = gatt.getService(serviceUUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                if (characteristic != null) {

                    for(BleMeshConnectedUser user : newBleMeshConnectedDevicesMap.values()){
                        message+=user.toInitString();
                        count++;

                        if(count!=0&&(count%sendSize==0||count==newBleMeshConnectedDevicesMap.size())){
                            characteristic.setValue(message);
                            gatt.writeCharacteristic(characteristic);
                            nowSize++;
                            message="change/"+myId+"/"+maxSize+"/"+nowSize+"/";
                            continue;
                        }

                        message+="@";
                    }
                }
            }
        }
    }
}
