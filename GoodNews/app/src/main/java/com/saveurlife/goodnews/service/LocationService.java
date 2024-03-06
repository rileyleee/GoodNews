package com.saveurlife.goodnews.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class LocationService {
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationService(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startLocationUpdates(LocationListener locationListener) {
        this.locationListener = locationListener;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
        }
    }

    public String getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없을 경우 로그 출력
            Log.e("LocationService", "ACCESS_FINE_LOCATION permission not granted");
            return "0/0";
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            return lastKnownLocation.getLatitude() + "/" + lastKnownLocation.getLongitude();
        } else {
            // 위치 정보를 가져오지 못했을 경우 로그 출력
            Log.e("LocationService", "Last known location is null");
            return "0/0";
        }
    }
}
