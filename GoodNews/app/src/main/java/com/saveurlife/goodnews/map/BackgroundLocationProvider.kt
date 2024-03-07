package com.saveurlife.goodnews.map

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.saveurlife.goodnews.authority.AuthorityActivity
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.service.UserDeviceInfoService
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class BackgroundLocationProvider(private val context: Context) {

    private val userDeviceInfoService = UserDeviceInfoService.getInstance(context)
    private val memberId = userDeviceInfoService.deviceId
    private var currentTime by Delegates.notNull<Long>()
    private val emergencyAlarmProvider = EmergencyAlarmProvider(context)


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
            LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(30)).build()
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
            Toast.makeText(context, "백그라운드 위치 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
            val i = Intent(context, AuthorityActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
    }

    // 사용자 위치 realm에 업데이트 (나중에 여기 말고 백그라운드에서 저장하는 게 맞음)
    private fun updateMemberLocation(
        location: Location,
        memberId: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            // Realm 인스턴스 열기
            val realm: Realm = Realm.open(GoodNewsApplication.realmConfiguration)

            try {
                // 데이터베이스 작업 수행
                realm.write {
                    Log.v("현재 memberId", memberId)
                    val memberToUpdate =
                        realm.query<Member>("memberId == $0", memberId).first().find()
                    val latestMember = memberToUpdate?.let { findLatest(it) }

                    // 업데이트 시각 보정(+9시간 처리-> 한국 시각)
                    currentTime = System.currentTimeMillis()
                    currentTime += TimeUnit.HOURS.toMillis(9)

                    val latestUpdate =
                        RealmInstant.from(currentTime / 1000, (currentTime % 1000).toInt())
                    latestMember?.let { member ->
                        member.latitude = location.latitude
                        member.longitude = location.longitude
                        member.lastUpdate = latestUpdate
                        Log.d("BackgroundLocationProvider", "위치 정보 realm에 업데이트 완료")
                    }
                }
            } catch (e: Exception) {
                // 예외 처리
                Log.e("BackgroundLocationProvider", "위치 정보 업데이트 중 오류 발생", e)
            } finally {
                // 작업 완료 후 Realm 인스턴스 닫기
                realm.close()
            }
        }
    }

    // 위치 업데이트 처리 함수
    private fun onLocationUpdated(location: Location) {

        // 로그에 위치 정보 기록
        Log.d("BackgroundLocationUpdate", "위치 업데이트: Lat=${location.latitude}, Lon=${location.longitude}")

        // 사용자의 위치에 따른 위험 정보 근접 알림
        emergencyAlarmProvider.getAlarmInfo()

        // 위치 정보를 mapfragment에 전달하여 위치 표시 되도록
        location?.let { location ->
            updateMemberLocation(location, memberId)
        }
        locationUpdateListener?.onLocationChanged(location)
    }


    // 위치 업데이트 중단
    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
        }
    }

    // MainActivity 공유하기 위한 클래스 간 통신
    fun setLocationUpdateListener(listener: LocationUpdateListener) {
        this.locationUpdateListener = listener
    }

}