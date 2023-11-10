package com.saveurlife.goodnews.ble.message;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;


import com.saveurlife.goodnews.ble.BleMeshConnectedUser;
import com.saveurlife.goodnews.service.LocationService;
import com.saveurlife.goodnews.service.UserDeviceInfoService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class SendMessageManager {
    private UUID serviceUUID;
    private UUID characteristicUUID;
    private UserDeviceInfoService userDeviceInfoService;
    private LocationService locationService;
    private String myId;
    private String myName = "김나연";

    private int sendSize = 5;

    public SendMessageManager(UUID serviceUUID, UUID characteristicUUID,
                              UserDeviceInfoService userDeviceInfoService, LocationService locationService) {
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
        this.userDeviceInfoService = userDeviceInfoService;
        this.locationService = locationService;
        this.myId = userDeviceInfoService.getDeviceId();
    }


    public String sendMessageInit(BluetoothGatt deviceGatt, Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String formattedDate = sdf.format(now);

        Map<String, BleMeshConnectedUser> newBleMeshConnectedDevicesMap = new HashMap<>();

        String[] location = locationService.getLastKnownLocation().split("/");

        BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(myId, myName, formattedDate, "1", Double.parseDouble(location[0]), Double.parseDouble(location[1]));

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
                        Log.i("sendMessageInit", result);

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

                    Log.i("spreadmessage", content);
                }
            }
        }
    }

    public String sendMessageBase(Map<String, BluetoothGatt> deviceGattMap) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String formattedDate = sdf.format(now);

        MessageBase messageBase = new MessageBase();
        messageBase.setMessageType("base");
        messageBase.setSenderId(myId);
        messageBase.setSenderName(myName);
        messageBase.setSendTime(formattedDate);
        messageBase.setHealthStatus("1");
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

    public void sendMessageHelp(Map<String, BluetoothGatt> deviceGattMap, String content) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String formattedDate = sdf.format(now);

        MessageHelp messageHelp = new MessageHelp();
        messageHelp.setMessageType("help");
        messageHelp.setSenderId(myId);
        messageHelp.setSenderName(myName);
        messageHelp.setSendTime(formattedDate);
        messageHelp.setHealthStatus("1");
        messageHelp.setLocation(locationService.getLastKnownLocation());
        messageHelp.setContent(content);

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

    public String sendMessageChat(Map<String, BluetoothGatt> deviceGattMap, String receiverId, String content) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");

        String formattedDate = sdf.format(now);

        MessageChat messageChat = new MessageChat();
        messageChat.setMessageType("chat");
        messageChat.setSenderId(myId);
        messageChat.setSenderName(myName);
        messageChat.setSendTime(formattedDate);
        messageChat.setHealthStatus("1");
        messageChat.setLocation(locationService.getLastKnownLocation());
        messageChat.setReceiverId(receiverId);
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

    public void sendMessageDisconnect(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic != null) {
                Log.i("sendMessageRemove", "sendMessageRemove");
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
            BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(myId, myName, formattedDate, "1", Double.parseDouble(location[0]), Double.parseDouble(location[1]));
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
                            Log.i("sendMessageChange", message);
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
