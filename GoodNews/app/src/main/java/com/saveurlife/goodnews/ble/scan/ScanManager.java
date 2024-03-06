package com.saveurlife.goodnews.ble.scan;

import static com.saveurlife.goodnews.ble.Common.DEVICEINFO_UUID;
import static com.saveurlife.goodnews.ble.Common.SERVICE_UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;

import androidx.lifecycle.MutableLiveData;

import com.saveurlife.goodnews.ble.BleMeshConnectedUser;
import com.saveurlife.goodnews.ble.ChatRepository;
import com.saveurlife.goodnews.ble.bleGattClient.BleGattCallback;
import com.saveurlife.goodnews.ble.message.SendMessageManager;
import com.saveurlife.goodnews.ble.service.BleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanManager {
    private static ScanManager instance;

    private BluetoothLeScanner mBluetoothLeScanner;
    private BleServiceScanCallback mBleScanCallback;


    private ArrayList<String> deviceArrayList;
    private ArrayList<String> deviceArrayListName;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private static Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap;
    private MutableLiveData<List<String>> deviceArrayListNameLiveData;



    public static ScanManager getInstance(BluetoothLeScanner mBluetoothLeScanner,
                                              ArrayList<String> deviceArrayList, ArrayList<String> deviceArrayListName,
                                              ArrayList<BluetoothDevice> bluetoothDevices,
                                              Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap,
                                              MutableLiveData<List<String>> deviceArrayListNameLiveData) {
        if (instance == null) {
            instance = new ScanManager(mBluetoothLeScanner, deviceArrayList, deviceArrayListName, bluetoothDevices, bleMeshConnectedDevicesMap, deviceArrayListNameLiveData);
        }
        return instance;
    }

    private ScanManager(BluetoothLeScanner mBluetoothLeScanner,
                       ArrayList<String> deviceArrayList, ArrayList<String> deviceArrayListName,
                       ArrayList<BluetoothDevice> bluetoothDevices,
                       Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap,
                       MutableLiveData<List<String>> deviceArrayListNameLiveData){

        this.mBluetoothLeScanner = mBluetoothLeScanner;
        this.deviceArrayList = deviceArrayList;
        this.deviceArrayListName = deviceArrayListName;
        this.bluetoothDevices = bluetoothDevices;
        this.bleMeshConnectedDevicesMap = bleMeshConnectedDevicesMap;
        this.deviceArrayListNameLiveData = deviceArrayListNameLiveData;
    }


    public void startScanning() {
        // 현재 실행 중인 스캔이 있는지 확인하고 중지
        if (mBluetoothLeScanner != null && mBleScanCallback != null) {
            mBluetoothLeScanner.stopScan(mBleScanCallback);
        }

        deviceArrayList.clear();
        deviceArrayListName.clear();
        bluetoothDevices.clear();

        // 기존 콜백 인스턴스를 재사용하거나 필요한 경우 새 인스턴스를 생성
        if (mBleScanCallback == null) {
            mBleScanCallback = new BleServiceScanCallback();
        }

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setLegacy(false)
                .setPhy(BluetoothDevice.PHY_LE_CODED)
                .build();

        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(SERVICE_UUID))
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(scanFilter);

        mBluetoothLeScanner.startScan(filters, settings, mBleScanCallback);

        scanHandler.post(removeExpiredDevicesRunnable);
    }

    public void stopScanning() {
        // BLE 스캐너가 있다면, 스캐닝 중지
        if (mBluetoothLeScanner != null && mBleScanCallback != null) {
            mBluetoothLeScanner.stopScan(mBleScanCallback);
            // 콜백을 null로 설정하여 재사용을 방지
            mBleScanCallback = null;
        }

        // Handler를 사용하여 반복 작업을 취소
        scanHandler.removeCallbacks(removeExpiredDevicesRunnable);

        // 필요한 경우, 여기에 다른 자원 정리 로직을 추가
    }


    private Map<String, Long> lastSeenMap = new HashMap<>();
    private static final long EXPIRATION_TIME_MS = 5000;
    private final Handler scanHandler = new Handler(Looper.getMainLooper());
    private final Runnable removeExpiredDevicesRunnable = new Runnable() {
        @Override
        public void run() {
            removeExpiredDevices();
            // 예: 10초마다 한 번씩 removeExpiredDevices를 호출합니다.
            scanHandler.postDelayed(this, 1000);
        }
    };

    public void removeExpiredDevices() {
        long currentTime = System.currentTimeMillis();
        List<String> devicesToRemove = new ArrayList<>();

        for (Map.Entry<String, Long> entry : lastSeenMap.entrySet()) {
            if (currentTime - entry.getValue() > EXPIRATION_TIME_MS) {
                devicesToRemove.add(entry.getKey());
            }
        }

        for (String deviceId : devicesToRemove) {
            int index = deviceArrayList.indexOf(deviceId);
            if (index != -1) {
                bluetoothDevices.remove(index);
                deviceArrayList.remove(index);
                deviceArrayListName.remove(index);
            }
            lastSeenMap.remove(deviceId);
        }

        if (!devicesToRemove.isEmpty()) {
            deviceArrayListNameLiveData.postValue(deviceArrayListName);
        }
    }





    public class BleServiceScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();

            // 광고 레코드에서 사용자 이름 데이터 가져오기
            byte[] userIdBytes = null;
            byte[] userNameBytes = null;
            if (result.getScanRecord() != null) {
                userIdBytes = result.getScanRecord().getServiceData(new ParcelUuid(SERVICE_UUID));
                userNameBytes = result.getScanRecord().getServiceData(new ParcelUuid(DEVICEINFO_UUID));
            }

            // 사용자 이름이 null이거나 비어 있을 경우 "Unknown Device"로 표시
            String deviceId = (userIdBytes != null && userIdBytes.length > 0) ? new String(userIdBytes) : "Unknown Device";
            String deviceName = (userNameBytes != null && userNameBytes.length > 0) ? new String(userNameBytes) : "Unknown Name";

            for (Map<String, BleMeshConnectedUser> bleMeshConnectedUserMap : bleMeshConnectedDevicesMap.values()) {
                if (bleMeshConnectedUserMap.containsKey(deviceId)) {
//                    return;
                }
            }

            int existingDeviceIndex = -1;
            for (int i = 0; i < bluetoothDevices.size(); i++) {
                if (bluetoothDevices.get(i).getAddress().equals(deviceAddress) || deviceArrayList.get(i).equals(deviceId)) {
                    existingDeviceIndex = i;
                    break;
                }
            }

            // 마지막 감지 시간 업데이트
            lastSeenMap.put(deviceId, System.currentTimeMillis());

            if (existingDeviceIndex != -1) {
                bluetoothDevices.set(existingDeviceIndex, device);
                deviceArrayList.set(existingDeviceIndex, deviceId);
                deviceArrayListName.set(existingDeviceIndex, deviceId + "/" + deviceName);
                //                deviceArrayListNameLiveData.postValue(deviceArrayListName); // new code to update LiveData
            } else {
                bluetoothDevices.add(device);
                deviceArrayList.add(deviceId);
                deviceArrayListName.add(deviceId + "/" + deviceName); // your existing code where you add devices
                deviceArrayListNameLiveData.postValue(deviceArrayListName); // new code to update LiveData
            }
        }
    }
}
