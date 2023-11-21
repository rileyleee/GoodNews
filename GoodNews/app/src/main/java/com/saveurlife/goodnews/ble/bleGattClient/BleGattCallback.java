package com.saveurlife.goodnews.ble.bleGattClient;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.saveurlife.goodnews.ble.BleMeshConnectedUser;
import com.saveurlife.goodnews.ble.ChatRepository;
import com.saveurlife.goodnews.ble.message.SendMessageManager;

import java.util.Map;

public class BleGattCallback extends BluetoothGattCallback {

    private String myId;
    private String myName;
    private ChatRepository chatRepository;
    private SendMessageManager sendMessageManager;
    private static Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap;



    public BleGattCallback(String myId, String myName,
                           ChatRepository chatRepository,
                           SendMessageManager sendMessageManager,
                           Map<String, Map<String, BleMeshConnectedUser>> bleMeshConnectedDevicesMap){
        this.myId = myId;
        this.myName = myName;
        this.chatRepository = chatRepository;
        this.sendMessageManager = sendMessageManager;
        this.bleMeshConnectedDevicesMap = bleMeshConnectedDevicesMap;
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i("BLE", "PHY updated successfully.");
        } else {
            Log.e("BLE", "Failed to update PHY. Error code: " + status);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            String[] parts = new String(characteristic.getValue()).split("/");
            String type = parts[0];
            if ("disconnect".equals(type)) {
                gatt.close();
            } else if ("chat".equals(type)) {
                chatRepository.addMessageToChatRoom(parts[7], parts[8], myId, myName, parts[9], parts[3], true);
            }

            Log.i("송신 메시지", new String(characteristic.getValue()));
        } else {
            Log.e("BLE", "Failed to send message to " + gatt.getDevice().getAddress() + ". Error code: " + status);
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i("BLE", "Connected to GATT server.");
            Log.i("BLE", "Attempting to start service discovery:" + gatt.discoverServices());

//                deviceGattMap.put(gatt.getDevice().getAddress(), gatt);
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i("BLE", "Disconnected from GATT server.");
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        boolean result = gatt.requestMtu(400);
        Log.i("BLE", "MTU change request result: " + result);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i("BLE", "Services discovered.");

            if (gatt.getDevice().getBondState() == 12) {
                sendMessageManager.sendMessageInit(gatt, bleMeshConnectedDevicesMap);
            }
        } else {
            Log.w("BLE", "onServicesDiscovered received: " + status);
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i("BLE", "MTU changed to: " + mtu);
            sendMessageManager.sendMessageInit(gatt, bleMeshConnectedDevicesMap);
        } else {
            Log.w("BLE", "MTU change failed, status: " + status);
        }
    }
}