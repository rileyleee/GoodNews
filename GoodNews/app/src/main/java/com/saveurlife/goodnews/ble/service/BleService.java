package com.saveurlife.goodnews.ble.service;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import static com.saveurlife.goodnews.ble.Common.CHARACTERISTIC_UUID;
import static com.saveurlife.goodnews.ble.Common.SERVICE_UUID;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import com.saveurlife.goodnews.GoodNewsApplication;
import com.saveurlife.goodnews.R;
import com.saveurlife.goodnews.ble.BleMeshConnectedUser;
import com.saveurlife.goodnews.ble.ChatRepository;
import com.saveurlife.goodnews.ble.CurrentActivityEvent;
import com.saveurlife.goodnews.ble.DangerInfoRealmRepository;
import com.saveurlife.goodnews.ble.GroupRepository;
import com.saveurlife.goodnews.ble.BleNotification;
import com.saveurlife.goodnews.ble.advertise.AdvertiseManager;
import com.saveurlife.goodnews.ble.bleGattClient.BleGattCallback;
import com.saveurlife.goodnews.ble.message.ChatDatabaseManager;
import com.saveurlife.goodnews.ble.message.GroupDatabaseManager;
import com.saveurlife.goodnews.ble.message.SendMessageManager;
import com.saveurlife.goodnews.ble.scan.ScanManager;
import com.saveurlife.goodnews.main.PreferencesUtil;
import com.saveurlife.goodnews.map.FamilyMemProvider;
import com.saveurlife.goodnews.models.ChatMessage;
import com.saveurlife.goodnews.service.LocationService;
import com.saveurlife.goodnews.service.UserDeviceInfoService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BleService extends Service {
    private BleNotification bleNotification;

    private DangerInfoRealmRepository dangerInfoRealmRepository=new DangerInfoRealmRepository();
    private FamilyMemProvider familyMemProvider = new FamilyMemProvider();
    private Set<String> familyMemIds;
    private AdvertiseManager advertiseManager;
    private ScanManager scanManager;
    private BleGattCallback bleGattCallback;

    private String nowChatRoomID = "";
    private PreferencesUtil preferencesUtil;
    private int alter = 1;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public static SendMessageManager sendMessageManager;
    private LocationService locationService;
    private UserDeviceInfoService userDeviceInfoService;


    private static String myId;
    private static String myName;

    private static String myFamilyId = "";
    private static List<String> myGroupIds = new ArrayList<>();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private ArrayList<String> deviceArrayList;
    private ArrayList<String> deviceArrayListName;
    private MutableLiveData<List<String>> deviceArrayListNameLiveData = new MutableLiveData<>();

    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>(); // 스캔한 디바이스 객체

    private ArrayList<String> bleConnectedDevicesArrayList;
    private MutableLiveData<List<String>> bleConnectedDevicesArrayListLiveData = new MutableLiveData<>();

    private static Map<String, BluetoothGatt> deviceGattMap = new HashMap<>(); // 나와 ble로 직접 연결된 디바이스 <주소, BluetoothGatt>

    private static Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap; // mesh network로 나와 연결된 디바이스들 <나와 직접 연결된 디바이스 address, 이 디바이스를 통해 연결되어 있는 유저 <userId, user>
    private MutableLiveData<Map<String, Map<String, BleMeshConnectedUser>>> bleMeshConnectedDevicesMapLiveData = new MutableLiveData<>();

    private BluetoothGattServer mGattServer;


    private HandlerThread handlerThread;
    private Handler handler;
    private static final int INTERVAL = 10000; // 30 seconds


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if ("ACTION_START_ADVERTISE_AND_SCAN".equals(action)) {
                Log.i(TAG, "onStartCommand: ");
                startAdvertiseAndScanAndAuto();
            }
        }
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        preferencesUtil = new PreferencesUtil(this);
        myName = preferencesUtil.getString("name", "이름 없음");

        locationService = new LocationService(this);
        userDeviceInfoService = UserDeviceInfoService.getInstance(this);
        myId = userDeviceInfoService.getDeviceId();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        // PreferencesUtil 인스턴스 생성
        PreferencesUtil preferencesUtil = new PreferencesUtil(this);
        // PreferencesUtil을 사용하여 status 값을 읽기
//            myStatus = preferencesUtil.getString("status", "4");

        if (!mBluetoothAdapter.isLeCodedPhySupported()) {
            return;
        } else {
        }

        deviceArrayList = new ArrayList<>();
        deviceArrayListName = new ArrayList<>();
        bleConnectedDevicesArrayList = new ArrayList<>();
        bleMeshConnectedDevicesMap = new HashMap<>();


        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mGattServer = bluetoothManager.openGattServer(this, mGattServerCallback);

        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(characteristic);
        mGattServer.addService(service);


        sendMessageManager = SendMessageManager.getInstance(SERVICE_UUID, CHARACTERISTIC_UUID, userDeviceInfoService, locationService, preferencesUtil, myName);

        advertiseManager = AdvertiseManager.getInstance(mBluetoothAdapter, mBluetoothLeAdvertiser, myId, myName);
        scanManager = ScanManager.getInstance(mBluetoothLeScanner, deviceArrayList, deviceArrayListName, bluetoothDevices, bleMeshConnectedDevicesMap, deviceArrayListNameLiveData);
        bleGattCallback = BleGattCallback.getInstance(myId, myName, chatRepository, sendMessageManager, bleMeshConnectedDevicesMap);

//        chatDatabaseManager.createFamilyMemInfo();

        bleNotification = BleNotification.getInstance(getApplicationContext());

    }

    // 블루투스 시작 버튼
    public void startAdvertiseAndScanAndAuto() {
        advertiseManager.startAdvertising();
        scanManager.startScanning();
        startAutoSendMessage();

        familyMemProvider.updateAllFamilyMemIds();
        familyMemIds = familyMemProvider.getAllFamilyMemIds();
    }


    public void connectOrDisconnect(String deviceId) {
        BluetoothDevice selectedDevice = bluetoothDevices.get(deviceArrayList.indexOf(deviceId));

        if (bleConnectedDevicesArrayList.contains(selectedDevice.getAddress())) {
            BluetoothGatt gatt = deviceGattMap.remove(selectedDevice.getAddress());
            if (gatt != null) {
                bleConnectedDevicesArrayList.remove(selectedDevice.getAddress());
                bleConnectedDevicesArrayListLiveData.postValue(bleConnectedDevicesArrayList);

                bleMeshConnectedDevicesMap.remove(selectedDevice.getAddress());
                Log.i("disconnect", Integer.toString(bleMeshConnectedDevicesMap.size()));
                bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);

                sendMessageManager.createDisconnectMessage(gatt);
                sendMessageManager.createChangeMessage(deviceGattMap, bleMeshConnectedDevicesMap);
            }
        } else {
            connectToDevice(selectedDevice);
        }
    }

    public BluetoothDevice getBluetoothDeviceById(String deviceId) {
        for (BluetoothDevice device : bluetoothDevices) {
            if (device.getAddress().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    public void disconnect(BluetoothDevice device) {
        if (device != null) {
            BluetoothGatt gatt = deviceGattMap.get(device.getAddress());
            if (gatt != null) {
                gatt.disconnect();
                deviceGattMap.remove(device.getAddress());
                // 필요한 경우 추가적인 연결 해제 로직
            }
        }
    }


    public void disconnect(int position) {
        String address = bleConnectedDevicesArrayList.get(position);

        BluetoothGatt gatt = deviceGattMap.remove(address);
        if (gatt != null) {
            bleConnectedDevicesArrayList.remove(address);
            bleConnectedDevicesArrayListLiveData.postValue(bleConnectedDevicesArrayList);

            bleMeshConnectedDevicesMap.remove(address);
            bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);

            deviceArrayListNameLiveData.postValue(deviceArrayListName);

            sendMessageManager.createDisconnectMessage(gatt);
            sendMessageManager.createChangeMessage(deviceGattMap, bleMeshConnectedDevicesMap);

        }
    }

    public void connectToDevice(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        BluetoothGatt bluetoothGatt = device.connectGatt(this, false, bleGattCallback, BluetoothDevice.TRANSPORT_AUTO, BluetoothDevice.PHY_LE_CODED);
        deviceGattMap.put(device.getAddress(), bluetoothGatt);
    }

    private void startAutoSendMessage() {
        if (handlerThread == null) {
            handlerThread = new HandlerThread("AutoMessageSenderHandlerThread");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                sendMessageBase();
                // 반복적으로 실행될 작업
                handler.postDelayed(this, INTERVAL);
            }
        }, INTERVAL);
    }

    public static void sendMessageBase() {
        sendMessageManager.createBaseMessage(deviceGattMap);
    }

    public void sendMessageHelp() {
        sendMessageManager.createHelpMessage(deviceGattMap);

//        sendMessageManager.createDangerInfoMessage(deviceGattMap, "1@2023-12-25T14:11:59.000Z@36.3504@127.2978@화재발생");


//        Date now = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
//        String formattedDate = sdf.format(now);
//        String groupId = "group" + myId + formattedDate;
//        sendMessageManager.createGroupInviteMessage(deviceGattMap, new ArrayList<String>(),
//                groupId, "그룹");
    }

    public void sendMessageChat(String receiverId, String receiverName, String content) {
        sendMessageManager.createChatMessage(deviceGattMap, receiverId, receiverName, content);
    }

    public void sendMessageGroupInvite(List<String> receiverIds, String groupId, String groupName) {
        sendMessageManager.createGroupInviteMessage(deviceGattMap, receiverIds, groupId, groupName);
    }

    private void spreadMessage(String address, String content) {
        Map<String, BluetoothGatt> spreadDeviceGattMap = new HashMap<>();
        spreadDeviceGattMap.putAll(deviceGattMap);
        spreadDeviceGattMap.remove(address);
        sendMessageManager.createSpreadMessage(spreadDeviceGattMap, content);
    }


    public LiveData<List<String>> getDeviceArrayListNameLiveData() {
        return deviceArrayListNameLiveData;
    }

    public LiveData<List<String>> getBleConnectedDevicesArrayListLiveData() {
        return bleConnectedDevicesArrayListLiveData;
    }

    public LiveData<Map<String, Map<String, BleMeshConnectedUser>>> getBleMeshConnectedDevicesArrayListLiveData() {
        return bleMeshConnectedDevicesMapLiveData;
    }

    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BLE", "Service added successfully");
            } else {
                Log.e("BLE", "Failed to add service. Status: " + status);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.i("onConnectionStateChange", "onConnectionStateChange");
            super.onConnectionStateChange(device, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                String deviceAddress = device.getAddress();
                if (!bleConnectedDevicesArrayList.contains(deviceAddress)) {
                    bleConnectedDevicesArrayList.add(deviceAddress);
                    bleConnectedDevicesArrayListLiveData.postValue(bleConnectedDevicesArrayList);

                    if (!deviceGattMap.containsKey(deviceAddress)) {
                        connectToDevice(device);
                    } else {
                        // 기존 BluetoothGatt 객체 재사용
                        BluetoothGatt gatt = deviceGattMap.get(deviceAddress);
                        // 필요한 경우 gatt 객체를 사용하여 통신
                    }
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                String disconnectedDevice = device.getAddress();
                if (!bleConnectedDevicesArrayList.contains(disconnectedDevice)) {
                    return;
                }
                bleConnectedDevicesArrayList.remove(disconnectedDevice);
                bleConnectedDevicesArrayListLiveData.postValue(bleConnectedDevicesArrayList);

                BluetoothGatt gatt = deviceGattMap.remove(device.getAddress());
                if (gatt != null) {
                    gatt.close();
                }

                Map<String, BleMeshConnectedUser> removedUsers = bleMeshConnectedDevicesMap.remove(device.getAddress());
                bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);

                for(String removedUserId : removedUsers.keySet()){
                    if(familyMemIds.contains(removedUserId)){
                        // 여기서 가족 연결 끊어짐 알람
                        Log.i("가족 연결 끊어짐 알람", removedUserId);
                        bleNotification.foresendFamilyNotification(removedUsers.get(removedUserId).getUserName(), false);
                    }
                }
                bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
                sendMessageManager.createChangeMessage(deviceGattMap, bleMeshConnectedDevicesMap);
            }
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);

            if (SERVICE_UUID.equals(characteristic.getService().getUuid()) && CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                String message = new String(value);
                Log.i("수신 메시지", message);

                String[] parts = message.split("/");
                String messageType = parts[0];
                String senderId = parts[1];


                if (senderId.equals(myId)) return;

                if (messageType.equals("init")) {
                    String maxSize = parts[2];
                    String nowSize = parts[3];
                    String content = parts[4];

                    String[] users = content.split("@");
                    Map<String, BleMeshConnectedUser> insert = new HashMap<>();

                    for (String user : users) {
                        String[] data = user.split("-");
                        String dataId = data[0];
                        if (dataId.equals(myId)) {
                            continue;
                        }

                        // 스캔 목록에 있는 디바이스 정보가 넘어오면 스캔화면에서 삭제
                        if (deviceArrayList.contains(dataId)) {
                            int removeIndex = deviceArrayList.indexOf(dataId);
                            deviceArrayList.remove(removeIndex);
                            deviceArrayListName.remove(removeIndex);
                            bluetoothDevices.remove(removeIndex);
                            deviceArrayListNameLiveData.postValue(deviceArrayListName);
                        }

                        Log.i("가족연결", familyMemIds.toString());
                        // 가족 연결
                        if(familyMemIds.contains(dataId)){
                            // 처음 연결된건지 확인
                            Boolean check = false;
                            for(Map<String, BleMeshConnectedUser> bleUsers : bleMeshConnectedDevicesMap.values()){
                                if(bleUsers.containsKey(data)){
                                    check = true;
                                    break;
                                }
                            }

                            if(!check){
                                // 여기서 가족 연결 알람
                                Log.i("가족 연결 알람", data[1]);
                                bleNotification.foresendFamilyNotification(data[1], true);
                            }
                        }

                        BleMeshConnectedUser meshConnectedUser = new BleMeshConnectedUser(dataId, data[1], data[2], data[3], Double.parseDouble(data[4]), Double.parseDouble(data[5]));
                        insert.put(dataId, meshConnectedUser);
                    }

                    if (!bleMeshConnectedDevicesMap.containsKey(device.getAddress())) {
                        checkMatchingIds(insert, familyMemIds, parts);
                        bleMeshConnectedDevicesMap.put(device.getAddress(), insert);
                        if (nowSize.equals(maxSize)) {
                            bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
                        }
                    } else if (nowSize.equals(maxSize)) {
                        bleMeshConnectedDevicesMap.get(device.getAddress()).putAll(insert);
                        bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
                    } else {
                        bleMeshConnectedDevicesMap.get(device.getAddress()).putAll(insert);
                    }

                    spreadMessage(device.getAddress(), message);
                }
                else if (messageType.equals("base")) {
                    BleMeshConnectedUser existingUser = null;
                    spreadMessage(device.getAddress(), message);
                    BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(senderId, parts[2], parts[3], parts[4], Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));

                    if (bleMeshConnectedDevicesMap.containsKey(device.getAddress())) {
                        bleMeshConnectedDevicesMap.get(device.getAddress()).put(senderId, bleMeshConnectedUser);
                        bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
                    }

                    // 가족 상태 렘 업데이트
                    if(familyMemIds.contains(senderId)){

                    }
                }
                else if (messageType.equals("help")) {
                    GoodNewsApplication goodNewsApplication = (GoodNewsApplication) getApplicationContext();
                    if (!goodNewsApplication.isInBackground()) {
                        bleNotification.foresendNotification(parts);
                    } else {
                        // 앱이 백그라운드에 있을 때 푸시 알림 보내기
                        String nameBack = parts.length > 2 ? parts[2] : "이름 없음";
                        bleNotification.sendNotification(nameBack);
                    }
                    spreadMessage(device.getAddress(), message);
                }
                else if (messageType.equals("chat")) {
                    GoodNewsApplication goodNewsApplication = (GoodNewsApplication) getApplicationContext();
                    String targetId = parts[7];
                    String targetName = parts[8];
                    String senderName = parts[2];
                    String content = parts[9];
                    String time = parts[3];

                    Boolean isRead = nowChatRoomID.equals(senderId) ? true : false;

                    if (myId.equals(targetId)) {
                        chatRepository.addMessageToChatRoom(senderId, senderName, senderId, senderName, content, time, isRead);

                        if (!senderId.equals(nowChatRoomID)) {
                            if (!goodNewsApplication.isInBackground()) {
                                bleNotification.foresendNotification(parts);
                            } else {
                                String nameBack = parts.length > 2 ? parts[2] : "이름 없음";
                                String contentBack = parts.length > 9 ? parts[9] : "내용 없음";
                                bleNotification.sendChatting(nameBack, contentBack);
                            }
                        }
                    }
//                    else if (myFamilyId.equals(targetId)) {
//                        chatRepository.addMessageToChatRoom(targetId, "가족", senderId, senderName, content, time, isRead);
//                        spreadMessage(device.getAddress(), message);
//                    }
//                    else if (myGroupIds.contains(targetId)) {
//                        chatRepository.addMessageToChatRoom(targetId, "그룹이름", senderId, senderName, content, time, isRead);
//                        if (!goodNewsApplication.isInBackground()) {
//                            foresendNotification(parts);
//                        } else {
//                            String nameBack = parts.length > 2 ? parts[2] : "이름 없음";
//                            String contentBack = parts.length > 9 ? parts[9] : "내용 없음";
//                            sendChatting(nameBack, contentBack);
//                        }
//
//                        spreadMessage(device.getAddress(), message);
//
//                    }
                    else {
                        spreadMessage(device.getAddress(), message);
                    }

                }
                else if (messageType.equals("invite")) {
                    ArrayList<String> groupMembers = new ArrayList<>(Arrays.asList(parts[2].split("@")));

                    if (groupMembers.contains(myId)) {
                        // 여기서 그룹에 참여
                        String groupId = parts[3];
                        String groupName = parts[4];

                        List<BleMeshConnectedUser> membersList = bleMeshConnectedDevicesMap.values().stream()
                                .flatMap(users -> groupMembers.stream().map(users::get).filter(Objects::nonNull))
                                .collect(Collectors.toList());

                        groupRepository.addMembersToGroup(groupId, groupName, membersList);
                    }

                    spreadMessage(device.getAddress(), message);

                }
                else if (messageType.equals("disconnect")) {
                    BluetoothGatt gatt = deviceGattMap.remove(device.getAddress());
                    gatt.close();

                    bleConnectedDevicesArrayList.remove(device.getAddress());
                    bleConnectedDevicesArrayListLiveData.postValue(bleConnectedDevicesArrayList);

                    Map<String, BleMeshConnectedUser> removedUsers = bleMeshConnectedDevicesMap.remove(device.getAddress());
                    bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);

                    for(String removedUserId : removedUsers.keySet()){
                        if(familyMemIds.contains(removedUserId)){
                            // 여기서 가족 연결 끊어짐 알람
                            Log.i("가족 연결 끊어짐 알람", removedUserId);
                            bleNotification.foresendFamilyNotification(removedUsers.get(removedUserId).getUserName(), false);
                        }
                    }

                    sendMessageManager.createChangeMessage(deviceGattMap, bleMeshConnectedDevicesMap);
                }
                else if (messageType.equals("change")) {
                    Log.i("bleMeshConnectedDevicesMap", message);
                    String maxSize = parts[2];
                    String nowSize = parts[3];
                    String content = parts[4];

                    String[] users = content.split("@");
                    Map<String, BleMeshConnectedUser> insert = new HashMap<>();
                    for (String user : users) {
                        String[] data = user.split("-");
                        String dataId = data[0];
                        if (dataId.equals(myId)) {
                            continue;
                        }

                        if (deviceArrayList.contains(dataId)) {
                            int removeIndex = deviceArrayList.indexOf(dataId);
                            deviceArrayList.remove(removeIndex);
                            deviceArrayListName.remove(removeIndex);
                            bluetoothDevices.remove(removeIndex);
                            deviceArrayListNameLiveData.postValue(deviceArrayListName);

                        }

                        if(familyMemIds.contains(dataId)){
                            // 처음 연결된건지 확인
                            Boolean check = false;
                            for(Map<String, BleMeshConnectedUser> bleUsers : bleMeshConnectedDevicesMap.values()){
                                if(bleUsers.containsKey(data)){
                                    check = true;
                                    break;
                                }
                            }

                            if(!check){
                                // 여기서 가족 연결 알람
                                Log.i("가족 연결 알람", dataId);
                                bleNotification.foresendFamilyNotification(data[1], true);
                            }
                        }


                        BleMeshConnectedUser meshConnectedUser = new BleMeshConnectedUser(dataId, data[1], data[2], data[3], Double.parseDouble(data[4]), Double.parseDouble(data[5]));
                        insert.put(dataId, meshConnectedUser);
                    }
                    if (nowSize.equals("1")) {
                        bleMeshConnectedDevicesMap.put(device.getAddress(), insert);
                        if (nowSize.equals(maxSize)) {
                            bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
                        }
                    } else if (nowSize.equals(maxSize)) {
                        bleMeshConnectedDevicesMap.get(device.getAddress()).putAll(insert);
                        bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
                    } else {
                        bleMeshConnectedDevicesMap.get(device.getAddress()).putAll(insert);
                    }

                    spreadMessage(device.getAddress(), message);
                }
                else if (messageType.equals("danger")){
                    // 여기서 렘에 위험정보 저장
                    String dangerInfo=parts[2];
                    dangerInfoRealmRepository.saveDangerInfoToRealm(dangerInfo);
                    spreadMessage(device.getAddress(), message);
                }
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            }
        }

        // ... 필요한 경우 다른 콜백 메서드 추가 ...
    };


    public void checkMatchingIds(Map<String, BleMeshConnectedUser> insert, Set<String> familyMemIds, String[] parts) {
        // LiveData의 현재 값을 가져옴
        Set<String> currentFamilyMemIds = familyMemIds;
        if (currentFamilyMemIds != null) {
            for (String id : currentFamilyMemIds) {
                if (insert.containsKey(id)) {
                    Log.i("Matched ID", id);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 광고 중지 로직
        advertiseManager.stopAdvertising();
        scanManager.stopScanning();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        // HandlerThread 종료
        if (handlerThread != null) {
            handlerThread.quitSafely();
            handlerThread = null;
        }

        // BluetoothGattServer 연결 닫기
        if (mGattServer != null) {
            mGattServer.close();
            mGattServer = null;
        }

        // 모든 BluetoothGatt 연결 닫기
        for (BluetoothGatt gatt : deviceGattMap.values()) {
            if (gatt != null) {
                gatt.close();
            }
        }
        deviceGattMap.clear();
        EventBus.getDefault().unregister(this);
    }

    //채팅
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCurrentActivityEvent(CurrentActivityEvent event) {
        String currentActivityName = event.getActivityName();
        if ("none".equals(currentActivityName)) {
            nowChatRoomID = currentActivityName;
        } else {
            nowChatRoomID = currentActivityName;
        }
    }


    //채팅
    private ChatDatabaseManager chatDatabaseManager = new ChatDatabaseManager();
    private ChatRepository chatRepository = new ChatRepository(chatDatabaseManager);

    public MutableLiveData<List<ChatMessage>> getChatRoomMessages(String chatRoomId) {
        return chatRepository.getChatRoomMessages(chatRoomId);
    }

    public MutableLiveData<List<ChatMessage>> getReadUpdatedChatRoomMessages(String chatRoomId) {
        return chatRepository.getReadUpdatedChatRoomMessages(chatRoomId);
    }

    public LiveData<List<String>> getAllChatRoomIds() {
        return chatRepository.getAllChatRoomIds();
    }


    //    private MutableLiveData<Map<String, Map<String, BleMeshConnectedUser>>> bleMeshConnectedDevicesMapLiveData = new MutableLiveData<>();
    public BleMeshConnectedUser getBleMeshConnectedUser(String userId) {
        BleMeshConnectedUser returnUser = null;
        for (Map<String, BleMeshConnectedUser> innerMap : bleMeshConnectedDevicesMap.values()) {
            if (innerMap.containsKey(userId)) {
                returnUser = innerMap.get(userId);
            }
        }
        return returnUser;
    }

    public MutableLiveData<BleMeshConnectedUser> getBleMeshConnectedUserWithId(String userId) {
        MutableLiveData<BleMeshConnectedUser> userLiveData = new MutableLiveData<>();

        bleMeshConnectedDevicesMapLiveData.observeForever(new Observer<Map<String, Map<String, BleMeshConnectedUser>>>() {
            @Override
            public void onChanged(Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap) {
                for (Map<String, BleMeshConnectedUser> innerMap : bleMeshConnectedDevicesMap.values()) {
                    if (innerMap.containsKey(userId)) {
                        userLiveData.setValue(innerMap.get(userId));
                        break; // 일치하는 사용자를 찾았으므로 반복 중단
                    }
                }
            }
        });

        return userLiveData;
    }


    private GroupDatabaseManager groupDatabaseManager = new GroupDatabaseManager();
    private GroupRepository groupRepository = new GroupRepository(groupDatabaseManager);

    public void addMembersToGroup(String groupName, List<String> members) {

        Map<String, BleMeshConnectedUser> allConnectedUser = new HashMap<>();
        for (Map<String, BleMeshConnectedUser> users : bleMeshConnectedDevicesMap.values()) {
            allConnectedUser.putAll(users);
            Log.i("연결된사용자수", Integer.toString(users.size()));
        }

        List<BleMeshConnectedUser> membersList = new ArrayList<>();
        for (String memberId : members) {
            if (allConnectedUser.containsKey(memberId)) {
                membersList.add(allConnectedUser.get(memberId));
            }
        }

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
        String formattedDate = sdf.format(now);
        String groupId = "group" + myId + formattedDate;

        groupRepository.addMembersToGroup(groupId, groupName, membersList);

        List<String> membersId = new ArrayList<>();
        for (BleMeshConnectedUser member : membersList) {
            membersId.add(member.getUserId());
        }

        sendMessageManager.createGroupInviteMessage(deviceGattMap, membersId, groupId, groupName);
    }


    public void createDangerInfoMessage(String dangerInfo){
        sendMessageManager.createDangerInfoMessage(deviceGattMap, dangerInfo);
    }



}