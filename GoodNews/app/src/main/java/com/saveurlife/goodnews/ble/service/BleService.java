package com.saveurlife.goodnews.ble.service;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import static com.saveurlife.goodnews.ble.Common.CHARACTERISTIC_UUID;
import static com.saveurlife.goodnews.ble.Common.DEVICEINFO_UUID;
import static com.saveurlife.goodnews.ble.Common.SERVICE_UUID;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import com.saveurlife.goodnews.GoodNewsApplication;
import com.saveurlife.goodnews.R;
import com.saveurlife.goodnews.ble.BleMeshConnectedUser;
//import com.saveurlife.goodnews.ble.message.ChatDatabaseManager;
import com.saveurlife.goodnews.ble.ChatRepository;
import com.saveurlife.goodnews.ble.CurrentActivityEvent;
//import com.saveurlife.goodnews.ble.GroupRepository;
import com.saveurlife.goodnews.ble.GroupRepository;
import com.saveurlife.goodnews.ble.advertise.AdvertiseManager;
import com.saveurlife.goodnews.ble.bleGattClient.BleGattCallback;
import com.saveurlife.goodnews.ble.message.ChatDatabaseManager;
//import com.saveurlife.goodnews.ble.message.GroupDatabaseManager;
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

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BleService extends Service {
    private FamilyMemProvider familyMemProvider = new FamilyMemProvider();
    private LiveData<List<String>> familyMemIds;
    private AdvertiseManager advertiseManager;
    private ScanManager scanManager;
    private BleGattCallback bleGattCallback;

    private String nowChatRoomID = "";
    private PreferencesUtil preferencesUtil;
    private int alter = 1;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BleService getService() {
            // Return this instance of BleService so clients can call public methods
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
        userDeviceInfoService = new UserDeviceInfoService(this);
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


        sendMessageManager = new SendMessageManager(SERVICE_UUID, CHARACTERISTIC_UUID, userDeviceInfoService, locationService, preferencesUtil, myName);

        advertiseManager = AdvertiseManager.getInstance(mBluetoothAdapter, mBluetoothLeAdvertiser, myId, myName);
        scanManager = ScanManager.getInstance(mBluetoothLeScanner, deviceArrayList, deviceArrayListName, bluetoothDevices, bleMeshConnectedDevicesMap, deviceArrayListNameLiveData);
        bleGattCallback = BleGattCallback.getInstance(myId, myName, chatRepository, sendMessageManager, bleMeshConnectedDevicesMap);

//        chatDatabaseManager.createFamilyMemInfo();

        familyMemProvider.updateAllFamilyMemIds();
        familyMemIds = familyMemProvider.getAllFamilyMemIds();
        Log.i("familyMemIds", familyMemIds.toString());
    }

    // 블루투스 시작 버튼
    public void startAdvertiseAndScanAndAuto() {
        advertiseManager.startAdvertising();
        scanManager.startScanning();
        startAutoSendMessage();
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

                sendMessageManager.sendMessageDisconnect(gatt);
                sendMessageManager.sendMessageChange(deviceGattMap, bleMeshConnectedDevicesMap);
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

            sendMessageManager.sendMessageDisconnect(gatt);
            sendMessageManager.sendMessageChange(deviceGattMap, bleMeshConnectedDevicesMap);

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
        sendMessageManager.sendMessageBase(deviceGattMap);
    }

    public void sendMessageHelp() {

        // 여기에 메시지 전송 로직을 구현합니다.
        sendMessageManager.sendMessageHelp(deviceGattMap);
    }

    public void sendMessageChat(String receiverId, String receiverName, String content) {
        // 여기에 메시지 전송 로직을 구현합니다.
        sendMessageManager.sendMessageChat(deviceGattMap, receiverId, receiverName, content);
    }

    public void sendMessageGroupInvite(List<String> receiverIds, String groupId, String groupName) {
        sendMessageManager.sendMessageGroupInvite(deviceGattMap, receiverIds, groupId, groupName);
    }

    private void spreadMessage(String address, String content) {
        Map<String, BluetoothGatt> spreadDeviceGattMap = new HashMap<>();
        spreadDeviceGattMap.putAll(deviceGattMap);
        spreadDeviceGattMap.remove(address);
        sendMessageManager.spreadMessage(spreadDeviceGattMap, content);
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
                    if(familyMemIds.getValue().contains(removedUserId)){
                        // 여기서 가족 연결 끊어짐 알람
                        Log.i("가족 연결 끊어짐 알람", removedUserId);
                        foresendFamilyNotification(removedUsers.get(removedUserId).getUserName(), false);
                    }
                }
                bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
                sendMessageManager.sendMessageChange(deviceGattMap, bleMeshConnectedDevicesMap);
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

                // 처음 연결 시 내 메시 네트워크 유저 정보 교환
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

                        // 가족 연결
                        if(familyMemIds.getValue().contains(dataId)){
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
                                foresendFamilyNotification(data[1], true);
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
                // 지속적 위치, 상태 정보 뿌리기
                else if (messageType.equals("base")) {
                    BleMeshConnectedUser existingUser = null;
                    spreadMessage(device.getAddress(), message);
                    BleMeshConnectedUser bleMeshConnectedUser = new BleMeshConnectedUser(senderId, parts[2], parts[3], parts[4], Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));

                    if (bleMeshConnectedDevicesMap.containsKey(device.getAddress())) {
                        bleMeshConnectedDevicesMap.get(device.getAddress()).put(senderId, bleMeshConnectedUser);
                        bleMeshConnectedDevicesMapLiveData.postValue(bleMeshConnectedDevicesMap);
                    }

                    // 가족 상태 렘 업데이트
                    if(familyMemIds.getValue().contains(senderId)){

                    }
                }
                // 모두에게 구조요청
                else if (messageType.equals("help")) {
                    GoodNewsApplication goodNewsApplication = (GoodNewsApplication) getApplicationContext();
                    if (!goodNewsApplication.isInBackground()) {
                        foresendNotification(parts);
                    } else {
                        // 앱이 백그라운드에 있을 때 푸시 알림 보내기
                        String nameBack = parts.length > 2 ? parts[2] : "이름 없음";
                        sendNotification(nameBack);
                    }
//                        sendNotification(message);
                    spreadMessage(device.getAddress(), message);
                }
                // 특정 대상에게 채팅
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
                                foresendNotification(parts);
                            } else {
                                String nameBack = parts.length > 2 ? parts[2] : "이름 없음";
                                String contentBack = parts.length > 9 ? parts[9] : "내용 없음";
                                sendChatting(nameBack, contentBack);
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
                        if(familyMemIds.getValue().contains(removedUserId)){
                            // 여기서 가족 연결 끊어짐 알람
                            Log.i("가족 연결 끊어짐 알람", removedUserId);
                            foresendFamilyNotification(removedUsers.get(removedUserId).getUserName(), false);
                        }
                    }

                    sendMessageManager.sendMessageChange(deviceGattMap, bleMeshConnectedDevicesMap);
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

                        if(familyMemIds.getValue().contains(dataId)){
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
                                foresendFamilyNotification(data[1], true);
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
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            }
        }

        // ... 필요한 경우 다른 콜백 메서드 추가 ...
    };


    public void checkMatchingIds(Map<String, BleMeshConnectedUser> insert, LiveData<List<String>> familyMemIds, String[] parts) {
        // LiveData의 현재 값을 가져옴
        List<String> currentFamilyMemIds = familyMemIds.getValue();
        if (currentFamilyMemIds != null) {
            for (String id : currentFamilyMemIds) {
                if (insert.containsKey(id)) {
                    Log.i("Matched ID", id);
                }
            }
        }
    }


    //구조요청 알림
    private void sendNotification(String messageContent) {
        // Notification Channel 생성 (Android O 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Notification Channel";
            String description = "Channel for My App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MY_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // 채널을 시스템에 등록
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "MY_CHANNEL_ID")
                .setSmallIcon(R.drawable.good_news_logo) // 알림 아이콘 설정
                .setContentTitle("구조 요청") // 알림 제목
                .setContentText(messageContent + "님이 구조를 요청했습니다.") // 'message'는 받은 메시지의 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 알림 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(alter++, builder.build()); // 'notificationId'는 각 알림을 구별하는 고유 ID

    }

    //포그라운드
    public void foresendNotification(String[] parts) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View layout = inflater.inflate(R.layout.custom_toast, null);
                View chatLayout = inflater.inflate(R.layout.custom_toast_chat, null);

                // 커스텀 레이아웃의 파라미터 설정
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(layoutParams);

                // 커스텀 레이아웃의 뷰에 접근하여 설정
                TextView senderName = layout.findViewById(R.id.toast_name);
                TextView time = layout.findViewById(R.id.toast_time);

                TextView nameChat = chatLayout.findViewById(R.id.toast_chat_name);
                TextView context = chatLayout.findViewById(R.id.toast_chat_text);
                TextView timeChat = chatLayout.findViewById(R.id.toast_chat_time);

                String name = parts.length > 2 ? parts[2] : "이름 없음";
                if (parts[0].equals("help")) {
                    String content = name + "님께서 구조를 요청했습니다.";
                    senderName.setText(content);

                } else if (parts[0].equals("chat")) {
                    nameChat.setText(name);
                    String content = parts.length > 9 ? parts[9] : "내용 없음";
                    context.setText(content);
                }


                String currentTime = new SimpleDateFormat("a hh:mm", Locale.KOREA).format(Calendar.getInstance().getTime());
                time.setText(currentTime);
                timeChat.setText(currentTime);

                // 시스템 알림 사운드 재생
                try {
//                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                                    r.play();
                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.toast_alarm);
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                if (parts[0].equals("help")) {
                    toast.setView(layout);
                } else if (parts[0].equals("chat")) {
                    toast.setView(chatLayout);
                }
                toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                toast.show();

//                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //채팅 알림(백그라운드)
    private void sendChatting(String messageContent, String contentBack) {
        // Notification Channel 생성 (Android O 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Notification Channel";
            String description = "Channel for My App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MY_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // 채널을 시스템에 등록
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "MY_CHANNEL_ID")
                .setSmallIcon(R.drawable.good_news_logo) // 알림 아이콘 설정
                .setContentTitle(messageContent) // 알림 제목
                .setContentText(contentBack) // 'message'는 받은 메시지의 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 알림 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(alter++, builder.build()); // 'notificationId'는 각 알림을 구별하는 고유 ID

    }


    //가족 알림(포그라운드)
    public void foresendFamilyNotification(String name, boolean isConnect) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View layout = inflater.inflate(R.layout.custom_toast_family, null);

                // 커스텀 레이아웃의 파라미터 설정
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(layoutParams);

                // 커스텀 레이아웃의 뷰에 접근하여 설정
                TextView senderName = layout.findViewById(R.id.toast_family);
                if(isConnect){
                    String content = "가족 " + name + "님이 연결되었습니다.";
                    senderName.setText(content);
                }else{
                    String content = "가족 " + name + "님과 연결이 끊겼습니다.";
                    senderName.setText(content);
                }



                // 시스템 알림 사운드 재생
                try {
                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.toast_alarm);
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
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

    public LiveData<List<String>> getAllChatRoomIds() {
        return chatRepository.getAllChatRoomIds();
    }

    public void updateIsReadStatus(String chatRoomId) {
        chatRepository.updateIsReadStatus(chatRoomId);
        chatRepository.getChatRoomMessages(chatRoomId);
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

        sendMessageManager.sendMessageGroupInvite(deviceGattMap, membersId, groupId, groupName);
    }
}