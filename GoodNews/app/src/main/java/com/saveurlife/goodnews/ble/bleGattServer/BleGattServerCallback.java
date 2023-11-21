//package com.saveurlife.goodnews.ble.bleGattServer;
//
//import static com.saveurlife.goodnews.ble.Common.CHARACTERISTIC_UUID;
//import static com.saveurlife.goodnews.ble.Common.SERVICE_UUID;
//
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattServerCallback;
//import android.bluetooth.BluetoothGattService;
//import android.bluetooth.BluetoothProfile;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.util.Log;
//
//import androidx.lifecycle.MutableLiveData;
//
//import com.saveurlife.goodnews.GoodNewsApplication;
//import com.saveurlife.goodnews.ble.BleMeshConnectedUser;
//import com.saveurlife.goodnews.ble.message.SendMessageManager;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//public class BleGattServerCallback extends BluetoothGattServerCallback {
//    private ArrayList<String> bleConnectedDevicesArrayList;
//    private MutableLiveData<ArrayList<String>> bleConnectedDevicesArrayListLiveData;
//    private Map<String, BluetoothGatt> deviceGattMap;
//    private Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap;
//    private MutableLiveData<Map<String, Map<String, BleMeshConnectedUser>>> bleMeshConnectedDevicesMapLiveData;
//
//
//    public SendMessageManager sendMessageManager;
//
//
//    private ArrayList<String> deviceArrayList;
//    private ArrayList<String> deviceArrayListName;
//    private ArrayList<BluetoothDevice> bluetoothDevices;
//    private MutableLiveData<List<String>> deviceArrayListNameLiveData;
//
//
//    private GoodNewsApplication goodNewsApplication;
//
//    private String myId;
//
//
//    // 생성자
//    public BleGattServerCallback(ArrayList<String> bleConnectedDevicesArrayList,
//                                 MutableLiveData<ArrayList<String>> bleConnectedDevicesArrayListLiveData,
//                                 Map<String, BluetoothGatt> deviceGattMap,
//                                 Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap,
//                                 MutableLiveData<Map<String, Map<String, BleMeshConnectedUser>>> bleMeshConnectedDevicesMapLiveData,
//                                 SendMessageManager sendMessageManager,
//
//                                 BluetoothLeScanner mBluetoothLeScanner,
//                                 ArrayList<String> deviceArrayList, ArrayList<String> deviceArrayListName,
//                                 ArrayList<BluetoothDevice> bluetoothDevices,
//                                 MutableLiveData<List<String>> deviceArrayListNameLiveData,
//
//                                 GoodNewsApplication goodNewsApplication,
//
//                                 String myId
//                                 ) {
//        this.bleConnectedDevicesArrayList = bleConnectedDevicesArrayList;
//        this.bleConnectedDevicesArrayListLiveData = bleConnectedDevicesArrayListLiveData;
//        this.deviceGattMap = deviceGattMap;
//        this.bleMeshConnectedDevicesMap = bleMeshConnectedDevicesMap;
//        this.bleMeshConnectedDevicesMapLiveData = bleMeshConnectedDevicesMapLiveData;
//        this.sendMessageManager = sendMessageManager;
//
//
//        this.deviceArrayList = deviceArrayList;
//        this.deviceArrayListName = deviceArrayListName;
//        this.bluetoothDevices = bluetoothDevices;
//        this.deviceArrayListNameLiveData = deviceArrayListNameLiveData;
//
//        this.goodNewsApplication = goodNewsApplication;
//
//        this.myId = myId;
//    }
//
//
//
//    @Override
//    public void onServiceAdded(int status, BluetoothGattService service) {
//        super.onServiceAdded(status, service);
//        if (status == BluetoothGatt.GATT_SUCCESS) {
//            Log.i("BLE", "Service added successfully");
//        } else {
//            Log.e("BLE", "Failed to add service. Status: " + status);
//        }
//    }
//
//    @Override
//    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
//        Log.i("onConnectionStateChange", "onConnectionStateChange");
//        super.onConnectionStateChange(device, status, newState);
//        if (newState == BluetoothProfile.STATE_CONNECTED) {
//            String deviceAddress = device.getAddress();
//            if (!bleConnectedDevicesArrayList.contains(deviceAddress)) {
//                bleConnectedDevicesArrayList.add(deviceAddress);
//                bleConnectedDevicesArrayListLiveData.postValue(bleConnectedDevicesArrayList);
//
//                if (!deviceGattMap.containsKey(deviceAddress)) {
//                    connectToDevice(device);
//                } else {
//                    // 기존 BluetoothGatt 객체 재사용
//                    BluetoothGatt gatt = deviceGattMap.get(deviceAddress);
//                    // 필요한 경우 gatt 객체를 사용하여 통신
//                }
//            }
//        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//            String disconnectedDevice = device.getAddress();
//            if (!bleConnectedDevicesArrayList.contains(disconnectedDevice)) {
//                return;
//            }
//            bleConnectedDevicesArrayList.remove(disconnectedDevice);
//            bleConnectedDevicesArrayListLiveData.postValue(bleConnectedDevicesArrayList);
//
//            BluetoothGatt gatt = deviceGattMap.remove(device.getAddress());
//            if (gatt != null) {
//                gatt.close();
//            }
//
//            bleMeshConnectedDevicesMap.remove(device.getAddress());
//            bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
//            sendMessageManager.sendMessageChange(deviceGattMap, bleMeshConnectedDevicesMap);
//        }
//    }
//
//    @Override
//    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
//        super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
//
//        if (SERVICE_UUID.equals(characteristic.getService().getUuid()) && CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
//            String message = new String(value);
//            Log.i("수신 메시지", message);
//
//            String[] parts = message.split("/");
//            String messageType = parts[0];
//            String senderId = parts[1];
//
//
//            if (senderId.equals(myId)) return;
//
//            // 처음 연결 시 내 메시 네트워크 유저 정보 교환
//            if (messageType.equals("init")) {
//                String maxSize = parts[2];
//                String nowSize = parts[3];
//                String content = parts[4];
//
//                String[] users = content.split("@");
//                Map<String, BleMeshConnectedUser> insert = new HashMap<>();
//                for (String user : users) {
//                    String[] data = user.split("-");
//                    String dataId = data[0];
//                    if (dataId.equals(myId)) {
//                        continue;
//                    }
//
//                    if (deviceArrayList.contains(dataId)) {
//                        int removeIndex = deviceArrayList.indexOf(dataId);
//                        deviceArrayList.remove(removeIndex);
//                        deviceArrayListName.remove(removeIndex);
//                        bluetoothDevices.remove(removeIndex);
//                        deviceArrayListNameLiveData.postValue(deviceArrayListName);
//                    }
//
//                    BleMeshConnectedUser existingUser = null;
//                    if (bleMeshConnectedDevicesMap.containsKey(device.getAddress())) {
//                        existingUser = bleMeshConnectedDevicesMap.get(device.getAddress()).get(senderId);
//                    }
//
//                    boolean isSelected = existingUser != null ? existingUser.getIsSelected() : false;
//
//                    BleMeshConnectedUser meshConnectedUser = new BleMeshConnectedUser(dataId, data[1], data[2], data[3], Double.parseDouble(data[4]), Double.parseDouble(data[5]), isSelected);
//                    insert.put(dataId, meshConnectedUser);
//                }
//                if (!bleMeshConnectedDevicesMap.containsKey(device.getAddress())) {
//                    bleMeshConnectedDevicesMap.put(device.getAddress(), insert);
//                    if (nowSize.equals(maxSize)) {
//                        bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
//                    }
//                } else if (nowSize.equals(maxSize)) {
//                    bleMeshConnectedDevicesMap.get(device.getAddress()).putAll(insert);
//                    bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
//                } else {
//                    bleMeshConnectedDevicesMap.get(device.getAddress()).putAll(insert);
//                }
//
//                spreadMessage(device.getAddress(), message);
//            }
//
//            // 지속적 위치, 상태 정보 뿌리기
//            else if (messageType.equals("base")) {
//                BleMeshConnectedUser existingUser = null;
//                if (bleMeshConnectedDevicesMap.containsKey(device.getAddress())) {
//                    existingUser = bleMeshConnectedDevicesMap.get(device.getAddress()).get(senderId);
//                }
//
//                boolean isSelected = existingUser != null ? existingUser.getIsSelected() : false;
////                    chatDatabaseManager.createChatMessage(senderId, senderId, parts[2], "parts[8]", parts[3]);
//                spreadMessage(device.getAddress(), message);
//                BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(senderId, parts[2], parts[3], parts[4], Double.parseDouble(parts[5]), Double.parseDouble(parts[6]), isSelected);
//
//                if (bleMeshConnectedDevicesMap.containsKey(device.getAddress())) {
//                    bleMeshConnectedDevicesMap.get(device.getAddress()).put(senderId, bleMeshConnectedUser);
//                    bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
//                }
//            }
//
//            // 모두에게 구조요청
//            else if (messageType.equals("help")) {
//                if (!goodNewsApplication.isInBackground()) {
//                    foresendNotification(parts);
//                } else {
//                    // 앱이 백그라운드에 있을 때 푸시 알림 보내기
//                    String nameBack = parts.length > 2 ? parts[2] : "이름 없음";
//                    sendNotification(nameBack);
//                }
////                        sendNotification(message);
//                spreadMessage(device.getAddress(), message);
//            }
//
//            // 특정 대상에게 채팅
//            else if (messageType.equals("chat")) {
//                String targetId = parts[7];
//                String targetName = parts[8];
//                String senderName = parts[2];
//                String content = parts[9];
//                String time = parts[3];
//
//                Boolean isRead = nowChatRoomID.equals(senderId) ? true : false;
//
//                if (myId.equals(targetId)) {
//                    chatRepository.addMessageToChatRoom(senderId, senderName, senderId, senderName, content, time, isRead);
//
//                    if (!senderId.equals(nowChatRoomID)) {
//                        if (!goodNewsApplication.isInBackground()) {
//                            foresendNotification(parts);
//                        } else {
//                            String nameBack = parts.length > 2 ? parts[2] : "이름 없음";
//                            String contentBack = parts.length > 9 ? parts[9] : "내용 없음";
//                            sendChatting(nameBack, contentBack);
//                        }
//                    }
//                } else if (myFamilyId.equals(targetId)) {
//                    chatRepository.addMessageToChatRoom(targetId, "가족", senderId, senderName, content, time, isRead);
//                    spreadMessage(device.getAddress(), message);
//                } else if (myGroupIds.contains(targetId)) {
//                    chatRepository.addMessageToChatRoom(targetId, "그룹이름", senderId, senderName, content, time, isRead);
//                    if (!goodNewsApplication.isInBackground()) {
//                        foresendNotification(parts);
//                    } else {
//                        String nameBack = parts.length > 2 ? parts[2] : "이름 없음";
//                        String contentBack = parts.length > 9 ? parts[9] : "내용 없음";
//                        sendChatting(nameBack, contentBack);
//                    }
//
//                    spreadMessage(device.getAddress(), message);
//
//                } else {
//                    spreadMessage(device.getAddress(), message);
//                }
//
//            } else if (messageType.equals("invite")) {
//                ArrayList<String> groupMembers = new ArrayList<>(Arrays.asList(parts[2].split("@")));
//
//                if (groupMembers.contains(myId)) {
//                    // 여기서 그룹에 참여
//                    String groupId = parts[3];
//                    String groupName = parts[4];
//
//                    List<BleMeshConnectedUser> membersList = bleMeshConnectedDevicesMap.values().stream()
//                            .flatMap(users -> groupMembers.stream().map(users::get).filter(Objects::nonNull))
//                            .collect(Collectors.toList());
//
////                        groupRepository.addMembersToGroup(groupId, groupName, membersList);
//                }
//
//                spreadMessage(device.getAddress(), message);
//
//            } else if (messageType.equals("disconnect")) {
//                BluetoothGatt gatt = deviceGattMap.remove(device.getAddress());
//                gatt.close();
//
//                bleConnectedDevicesArrayList.remove(device.getAddress());
//                bleConnectedDevicesArrayListLiveData.postValue(bleConnectedDevicesArrayList);
//
//                bleMeshConnectedDevicesMap.remove(device.getAddress());
//                bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
//
//                sendMessageManager.sendMessageChange(deviceGattMap, bleMeshConnectedDevicesMap);
//            } else if (messageType.equals("change")) {
//                Log.i("bleMeshConnectedDevicesMap", message);
//                String maxSize = parts[2];
//                String nowSize = parts[3];
//                String content = parts[4];
//
//                String[] users = content.split("@");
//                Map<String, BleMeshConnectedUser> insert = new HashMap<>();
//                for (String user : users) {
//                    String[] data = user.split("-");
//                    String dataId = data[0];
//                    if (dataId.equals(myId)) {
//                        continue;
//                    }
//
//                    if (deviceArrayList.contains(dataId)) {
//                        int removeIndex = deviceArrayList.indexOf(dataId);
//                        deviceArrayList.remove(removeIndex);
//                        deviceArrayListName.remove(removeIndex);
//                        bluetoothDevices.remove(removeIndex);
//                        deviceArrayListNameLiveData.postValue(deviceArrayListName);
//
//                    }
//                    BleMeshConnectedUser existingUser = null;
//                    if (bleMeshConnectedDevicesMap.containsKey(device.getAddress())) {
//                        existingUser = bleMeshConnectedDevicesMap.get(device.getAddress()).get(senderId);
//                    }
//
//                    boolean isSelected = existingUser != null ? existingUser.getIsSelected() : false;
//                    BleMeshConnectedUser meshConnectedUser = new BleMeshConnectedUser(dataId, data[1], data[2], data[3], Double.parseDouble(data[4]), Double.parseDouble(data[5]), isSelected);
//                    insert.put(dataId, meshConnectedUser);
//                }
//                if (nowSize.equals("1")) {
//                    bleMeshConnectedDevicesMap.put(device.getAddress(), insert);
//                    if (nowSize.equals(maxSize)) {
//                        bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
//                    }
//                } else if (nowSize.equals(maxSize)) {
//                    bleMeshConnectedDevicesMap.get(device.getAddress()).putAll(insert);
//                    bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
//                } else {
//                    bleMeshConnectedDevicesMap.get(device.getAddress()).putAll(insert);
//                }
//
//                spreadMessage(device.getAddress(), message);
//            }
//            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
//        }
//    }
//}
