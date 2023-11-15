package com.saveurlife.goodnews.map

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.saveurlife.goodnews.common.SharedViewModel

class ConnectedUserProvider(
    private val sharedViewModel: SharedViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) {

    fun showConnectedUsers(){
        Log.v("connectedUser", "유저 리스트 목록 함수를 호출했습니다.")
        // LiveData를 관찰
        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(
            viewLifecycleOwner,
            Observer { connectedDevicesMap ->
                // 중첩된 맵에서 BleMeshConnectedUser 추출
                val users = connectedDevicesMap.flatMap { it.value.values }.toList()
                // 이제 'users'는 List<BleMeshConnectedUser>를 가지고 있으며 필요한 대로 사용할 수 있습니다.
                Log.v("connectedUser", "$users")

                users.forEach { user ->
                    val userToString = user.toString()
                    val dataParts = userToString.split("/")
                    val name = dataParts.getOrNull(1) ?: "모름"
                    val lat = dataParts.getOrNull(4) ?: "모름"
                    val lon = dataParts.getOrNull(5) ?: "모름"

                    Log.v("connectedUserData", "$name, $lat, $lon")
                }

                // 사용자 리스트를 가지고 무언가 업데이트해야 한다면, 여기에서 하세요.
            })
    }


}