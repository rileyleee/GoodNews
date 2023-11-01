package com.saveurlife.goodnews.map

import android.app.Activity
import android.widget.Toast
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import java.lang.ref.WeakReference

class LocationPermissionHelper(val activityRef: WeakReference<Activity>) {
    // 맵 박스에서 제공하는 위치 권한 요청을 도와주는 클래스
    private lateinit var permissionsManager: PermissionsManager

    // 위치 권한 확인하고 필요한 경우 사용자에게 권한을 요청
    fun checkPermissions(onMapReady: () -> Unit) {
        activityRef.get()?.let { activity: Activity ->
            // 위치 권한이 존재하는 경우
            if (PermissionsManager.areLocationPermissionsGranted(activity)) {
                onMapReady()
            } else { //위치 권한이 없는 경우 권한 요청
                permissionsManager = PermissionsManager(object : PermissionsListener {
                    // 권한 요청 이유를 설명하고자 할 때 호출
                    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                        activityRef.get()?.let {
                            Toast.makeText(
                                it, "위치 정보 수집 및 활용에 동의해주시기 바랍니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    //권한 요청 결과 받았을 때 호출
                    override fun onPermissionResult(granted: Boolean) {
                        activityRef.get()?.let {
                            if (granted) { // 권한이 허용된 경우 지도 준비
                                onMapReady()
                            } else { // 거부된 경우 activity 종료
                                it.finish()
                            }
                        }
                    }
                })
                // 위치 권한 요청 시작
                permissionsManager.requestLocationPermissions(activity)
            }
        }
    }

    // 권한 요청 이후 응답 onRequestPermissionsResult() 결과를 받아와서 PermissionsManager에 전달
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}