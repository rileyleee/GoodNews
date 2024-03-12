package com.saveurlife.goodnews.ble.advertise;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.saveurlife.goodnews.ble.Common.DEVICEINFO_UUID;
import static com.saveurlife.goodnews.ble.Common.SERVICE_UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.util.Log;
import java.nio.charset.StandardCharsets;

public class AdvertiseManager {
    private static AdvertiseManager instance;
    private boolean isAdvertising = false;

    private AdvertisingSetCallback bleAdvertisingSetCallback;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private String myId;
    private String myName;

    public static AdvertiseManager getInstance(BluetoothAdapter mBluetoothAdapter, BluetoothLeAdvertiser mBluetoothLeAdvertiser,
                                               String myId, String myName) {
        instance = new AdvertiseManager(mBluetoothAdapter, mBluetoothLeAdvertiser, myId, myName);
        return instance;
    }


    private AdvertiseManager(BluetoothAdapter mBluetoothAdapter, BluetoothLeAdvertiser mBluetoothLeAdvertiser,
                            String myId, String myName){
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.mBluetoothLeAdvertiser = mBluetoothLeAdvertiser;
        this.myId = myId;
        this.myName = myName;
    }

    public void startAdvertising() {
        if (isAdvertising) {
            Log.i(TAG, "Already advertising, not starting new advertisement.");
            return; // 이미 광고 중이면 여기서 리턴
        }

        byte[] userIdBytes = myId.getBytes(StandardCharsets.UTF_8);
        byte[] userNameBytes = myName.getBytes(StandardCharsets.UTF_8);

        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .addServiceUuid(new ParcelUuid(SERVICE_UUID))
                .addServiceData(new ParcelUuid(SERVICE_UUID), userIdBytes)
                .addServiceData(new ParcelUuid(DEVICEINFO_UUID), userNameBytes)
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .build();

        if (mBluetoothAdapter.isLeExtendedAdvertisingSupported()) { // Check if extended advertising is supported (Bluetooth 5.1+)
            AdvertisingSetParameters advertisingSetParameters;
            if(mBluetoothAdapter.isLeCodedPhySupported()){
                advertisingSetParameters = new AdvertisingSetParameters.Builder()
                        .setLegacyMode(false)
                        .setConnectable(true)
                        .setInterval(AdvertisingSetParameters.INTERVAL_MIN)
                        .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MAX)
                        .setPrimaryPhy(BluetoothDevice.PHY_LE_CODED)
                        .setSecondaryPhy(BluetoothDevice.PHY_LE_CODED)
                        .build();
            }
            else{
                advertisingSetParameters = new AdvertisingSetParameters.Builder()
                        .setLegacyMode(false)
                        .setConnectable(true)
                        .setInterval(AdvertisingSetParameters.INTERVAL_MIN)
                        .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MAX)
                        .setPrimaryPhy(BluetoothDevice.PHY_LE_1M)
                        .setSecondaryPhy(BluetoothDevice.PHY_LE_1M)
                        .build();
            }

            AdvertiseData scanResponse = new AdvertiseData.Builder().build();

            bleAdvertisingSetCallback = new AdvertisingSetCallback() {
                @Override
                public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                    super.onAdvertisingSetStarted(advertisingSet, txPower, status);
                    isAdvertising = true; // 광고 상태를 '광고 중'으로 변경
                    Log.i(TAG, "Started extended advertising.");
                }

                @Override
                public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                    super.onAdvertisingSetStopped(advertisingSet);
                    isAdvertising = false; // 광고 상태를 '광고 중지'로 변경
                    Log.i(TAG, "Stopped extended advertising.");
                }

                // ... 필요한 다른 콜백 메소드 ...
            };

            mBluetoothLeAdvertiser.startAdvertisingSet(
                    advertisingSetParameters,
                    advertiseData,
                    scanResponse,
                    null,
                    null,
                    0,
                    0,
                    bleAdvertisingSetCallback
            );
        } else { // For Bluetooth 5.0 and below
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setConnectable(true)
                    .build();

            AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    isAdvertising = true; // 광고 상태를 '광고 중'으로 변경
                    Log.i("BLE", "Advertise success (Legacy)");
                }

                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);
                    isAdvertising = false; // 광고 상태를 '광고 중지'로 변경
                    Log.e("BLE", "Advertise failed (Legacy), error code: " + errorCode);
                }
            };
            mBluetoothLeAdvertiser.startAdvertising(settings, advertiseData, advertiseCallback);
        }
    }

    public void stopAdvertising() {
        if (!isAdvertising) {
            Log.i(TAG, "No advertising to stop, since it wasn't started.");
            return; // 광고 중이 아니면 여기서 리턴
        }
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertisingSet(bleAdvertisingSetCallback);
            Log.i(TAG, "Bluetooth advertising stopped.");
        }
        isAdvertising = false; // 광고 상태를 '광고 중지'로 변경
    }
}
