package com.saveurlife.goodnews.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.map.BackgroundLocationProvider
import org.osmdroid.util.GeoPoint

class LocationTrackingService : Service(), BackgroundLocationProvider.LocationUpdateListener  {

    private lateinit var backgroundLocationProvider: BackgroundLocationProvider
    private lateinit var currGeoPoint: GeoPoint

    val CHANNEL_ID = "ForegroundLocationTrackingCh"


    override fun onBind(intent: Intent?): IBinder {
        return Binder()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
    // 포어그라운드서비스 동작 알림 생성
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("foregroundService","알림 생성 호출!")
        createNotificationChannel()
        val notification: Notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("희소식이 안전을 위해 위치 정보를 저장하고 있습니다.")
                .setSmallIcon(
                    R.mipmap.ic_launcher_round
                )
                .build()
        startForeground(1, notification)

        // 현재 내 위치 정보 제공자
        backgroundLocationProvider = BackgroundLocationProvider(this)
        backgroundLocationProvider.initLocationClient()

        // 콜백 설정
        backgroundLocationProvider.setLocationUpdateListener(this)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onLocationChanged(location: Location) {
        currGeoPoint = GeoPoint(location.latitude, location.longitude)
        Log.v("위치가 변경되었습니다","$currGeoPoint")
    }

    fun stopTracking() {
        backgroundLocationProvider.stopLocationUpdates()
        stopForeground(Service.STOP_FOREGROUND_REMOVE) // 서비스를 백그라운드로 전환하고 알림을 제거
        stopSelf() // 서비스 인스턴스 종료
    }

}