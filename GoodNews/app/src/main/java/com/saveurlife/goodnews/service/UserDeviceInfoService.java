package com.saveurlife.goodnews.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class UserDeviceInfoService {
    private Context context;
    public UserDeviceInfoService(Context context) {
        this.context = context;
    }

    public String getDeviceId(){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            String phoneNumber = telephonyManager.getLine1Number();
//            Log.i("phoneNumber", phoneNumber); // 전화번호 없는 기기의 경우 오류 발생
            if (phoneNumber != null) {
                if (phoneNumber.startsWith("+82"))
                    phoneNumber = phoneNumber.replace("+82", "0");
                return phoneNumber;
            } else {
                return "번호가 유효하지 않습니다.";
            }
        } else {

            return "권한이 없습니다.";
        }
    }
}