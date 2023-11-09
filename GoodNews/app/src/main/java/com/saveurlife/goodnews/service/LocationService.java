package com.saveurlife.goodnews.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                return lastKnownLocation.getLatitude() + "/" + lastKnownLocation.getLongitude();
            }
        }
        return "0/0";
    }
}
