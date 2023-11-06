package com.saveurlife.goodnews.main

import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsUtil(private val activity: Activity) {
    companion object {
        const val PERMISSIONS_REQUEST_CODE_ALL = 100
        const val PERMISSIONS_REQUEST_CODE_BACKGROUND = 101
    }
    private var dialog: AlertDialog? = null

    // 앱 사용 위한 권한 요청(백그라운드 위치 정보 액세스 권한은 별도로 처리)
    fun requestAllPermissions() {
        val allPermissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.READ_PHONE_NUMBERS
        )

        // 안드로이드 12 스노우콘 (API 레벨 31)부터 명시적 권한 요청 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            allPermissions.add(android.Manifest.permission.BLUETOOTH_SCAN)
            allPermissions.add(android.Manifest.permission.BLUETOOTH_CONNECT)
            allPermissions.add(android.Manifest.permission.BLUETOOTH_ADMIN)
            allPermissions.add(android.Manifest.permission.BLUETOOTH_ADVERTISE)

        }
        // 안드로이드 13 티라미수 (API 레벨 33)부터 명시적 권한 요청 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            allPermissions.add(android.Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        val permissionsToRequest = allPermissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }


        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest.toTypedArray(),
                PERMISSIONS_REQUEST_CODE_ALL
            )
        } else {
            onAllPermissionsGranted()
            permissionDialog(activity)
        }
    }


    // 백그라운드 권한 요청 (안드로이드 API 30 버전부터 적용)
    private fun backgroundPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            PERMISSIONS_REQUEST_CODE_BACKGROUND
        )
    }

    fun permissionDialog(activity: Activity) {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요")

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when (p1) {
                DialogInterface.BUTTON_POSITIVE ->
                    backgroundPermission(activity)
            }
        }
        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", null)
        dialog = builder.create()
        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }

    fun onAllPermissionsGranted() {
        Toast.makeText(activity, "앱을 사용하기 위한 모든 권한이 승인되었습니다.", Toast.LENGTH_LONG).show()
    }

    fun onPermissionsDenied(deniedPermissions: List<String>) {
        Log.d("ㅋㅋㅋㅋ",deniedPermissions.toString())
        Toast.makeText(
            activity,
            ": $deniedPermissions",
            Toast.LENGTH_LONG
        ).show()
        activity.finish()
    }

    //권한 승인 확인
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ALL) {
            val deniedPermissions = permissions.filterIndexed { index, _ -> grantResults[index] != PackageManager.PERMISSION_GRANTED }
            if (deniedPermissions.isEmpty()) {
                onAllPermissionsGranted()
            } else {
                onPermissionsDenied(deniedPermissions)
            }
        }
    }

}