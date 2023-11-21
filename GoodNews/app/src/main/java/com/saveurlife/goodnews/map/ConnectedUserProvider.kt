package com.saveurlife.goodnews.map

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.common.SharedViewModel

class ConnectedUserProvider(
    private val sharedViewModel: SharedViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) {
    fun provideConnectedUsers(callback: (List<BleMeshConnectedUser>) -> Unit) {


        Log.v("connectedUser", "유저 리스트 목록 함수를 호출했습니다.")
        // LiveData를 관찰
        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(
            viewLifecycleOwner,
            Observer { connectedDevicesMap ->
                val userList = mutableListOf<BleMeshConnectedUser>()
                // 중첩된 맵에서 BleMeshConnectedUser 추출
                var users = connectedDevicesMap.flatMap { it.value.values }.toList()

                Log.v("connectedUser", "$users")

                // BLE로 연결된 내역에서 가족인 경우에는 필터링해서 realm에 저장하는 방식으로
                // 마커가 두 번 찍히지 않게 처리

                users.forEach { user ->
                    try{
                    val userToString = user.toString()
                    val userData = userToString.split("/")
                        val userId = userData[0]
                        val userName = userData[1]
                        val updateTime = userData[2]
                        val healthStatus = userData[3]
                        val lat = userData[4].toDouble()
                        val lon = userData[5].toDouble()
                        Log.v(
                            "connectedUserData",
                            "$userId, $userName, $updateTime, $healthStatus, $lat, $lon"
                        )
                        Log.d("connectedUserProvider","BleMeshConnectedUser 객체로 복사 중")
                    // userList에 담기
                    userList.add(                        
                        BleMeshConnectedUser( userData[0], userData[1], userData[2], userData[3], userData[4].toDouble(), userData[5].toDouble(),false)
                    )}
                    catch(e: Exception){
                        Log.e("ConnectedUserProvider", "연결된 이용자 데이터를 객체로 복사 중 오류 발생", e)
                    }
                }
                callback(userList)
            })
    }
}