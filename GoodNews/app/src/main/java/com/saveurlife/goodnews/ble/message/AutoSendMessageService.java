package com.saveurlife.goodnews.ble.message;



import static com.saveurlife.goodnews.ble.Common.CHARACTERISTIC_UUID;
import static com.saveurlife.goodnews.ble.Common.SERVICE_UUID;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.saveurlife.goodnews.main.PreferencesUtil;
import com.saveurlife.goodnews.service.LocationService;
import com.saveurlife.goodnews.service.UserDeviceInfoService;


public class AutoSendMessageService extends Service {
    private LocationService locationService;
    private UserDeviceInfoService userDeviceInfoService;
    private SendMessageManager sendMessageManager;
    private static final int INTERVAL = 30000; // 10 seconds
    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        // SharedPreferences에서 status 값을 읽기
        // PreferencesUtil 인스턴스 생성
        PreferencesUtil preferencesUtil = new PreferencesUtil(this);
        // PreferencesUtil을 사용하여 status 값을 읽기
        String myStatus = preferencesUtil.getString("status", "4");

        locationService = new LocationService(this);
        userDeviceInfoService = new UserDeviceInfoService(this);
        sendMessageManager = new SendMessageManager(SERVICE_UUID, CHARACTERISTIC_UUID, userDeviceInfoService, locationService, preferencesUtil);

        handlerThread = new HandlerThread("MessageServiceHandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendMessageBase();
                handler.postDelayed(this, INTERVAL);
            }
        }, INTERVAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void sendMessageBase() {
        // Fetch the deviceGattMap from the MainActivity or pass it via the Intent
//        Map<String, BluetoothGatt> deviceGattMap = MainActivity.getDeviceGattMap();
//        String result = sendMessageManager.sendMessageBase(deviceGattMap);
    }
}
