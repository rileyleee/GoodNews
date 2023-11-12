package com.saveurlife.goodnews.map

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.main.PermissionsUtil
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.service.UserDeviceInfoService
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LocationProvider(private val context: Context) {

    interface LocationUpdateListener {
        fun onLocationChanged(location: Location)
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateListener: LocationUpdateListener? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                onLocationUpdated(location)
            }
        }
    }

    // 위치 정보 제공자 초기 설정
    fun initLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        locationRequest =
            LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(10)).build()
        Log.d("LocationRequest", "위치 정보 요청 성공적으로 설정되었음: $locationRequest")

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Log.d("LocationSettings", "모든 위치 업데이트 관련 설정 완료")
        }

        task.addOnFailureListener { e ->
            Log.e("LocationSettings", "모든 위치 업데이트 관련 설정 미완: ${e.message}")
            if (e is ResolvableApiException) {
                try {
                    // context가 Activity의 인스턴스일 경우에만 startResolutionForResult() 호출
                    if (context is Activity) {
                        e.startResolutionForResult(context, 100)
                    } else {
                        Log.e("LocationProvider", "Context is not an Activity instance.")
                    }
                } catch (sendEx: IntentSender.SendIntentException) {
                    // 에러를 무시함.
                }
            }
        }
        requestingLocation()
    }

    // 위치 정보 요청
    private fun requestingLocation() {

        // 위치 업데이트 요청 시작(실시간 위치 수신)
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            // 권한 없을 경우 안내하기 (우리는 권한 없을 경우 앱을 아예 사용할 수 없는데 꼭 사용해야 할까)
            Toast.makeText(context, "권한 오류 발생", Toast.LENGTH_SHORT).show()
        }
    }



    // 위치 업데이트 처리 함수
    private fun onLocationUpdated(location: Location) {

        // 로그에 위치 정보 기록
        Log.d("LocationUpdate", "위치 업데이트: Lat=${location.latitude}, Lon=${location.longitude}")

        // 위치 정보를 mapfragment에 전달하여 위치 표시 되도록
        locationUpdateListener?.onLocationChanged(location)
    }

    // 마지막 위치 요청
    fun requestLastLocation() { // 권한 한 번 더 확인하고
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없으면 함수 종료
            Toast.makeText(
                context,
                "위치 정보 권한 없음",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // 마지막으로 알려진 위치 정보 요청
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationUpdated(it)
                }
            }
    }

    // 위치 업데이트 중단
    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
        }
    }

    // MapFragment와 공유하기 위한 클래스 간 통신
    fun setLocationUpdateListener(listener: LocationUpdateListener) {
        this.locationUpdateListener = listener
    }

}