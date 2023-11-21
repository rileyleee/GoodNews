package com.saveurlife.goodnews.map

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.models.FamilyMemInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class ConnectedUserProvider(
    private val sharedViewModel: SharedViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) {

    private val familyList = mutableListOf<FamilyMemInfo>()
    private var isFamilyMember = false

    fun getFamilyList(): MutableList<FamilyMemInfo> {
        val realm = Realm.open(GoodNewsApplication.realmConfiguration)

        val resultList = realm.query<FamilyMemInfo>().find()

        if (!resultList.isEmpty()) {
            resultList.forEach { fam -> familyList.add(fam) }
        }
        realm.close()

        return familyList
    }

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
                    try {
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
                        Log.d("connectedUserProvider", "BleMeshConnectedUser 객체로 복사 중")


                        // 가족 리스트에 해당 유저의 ID가 존재하는지 확인
                        val isFamilyMember = familyList.any { fam -> userId == fam.id }

                        if (!isFamilyMember) {
                            // userList에 담기
                            userList.add(
                                BleMeshConnectedUser(
                                    userId,
                                    userName,
                                    updateTime,
                                    healthStatus,
                                    lat,
                                    lon,
                                    false
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("ConnectedUserProvider", "연결된 이용자 데이터를 객체로 복사 중 오류 발생", e)
                    }
                }
                callback(userList)
            })
    }
}