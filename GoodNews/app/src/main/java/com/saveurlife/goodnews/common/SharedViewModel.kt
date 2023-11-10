package com.saveurlife.goodnews.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleMeshAdvertiseData

class SharedViewModel : ViewModel() {
    val isOnFlash: MutableLiveData<Boolean> = MutableLiveData(false)

//    val bleDeviceNames: MutableLiveData<List<String>> = MutableLiveData()

    val bleDeviceMap: MutableLiveData<Map<String, String>> = MutableLiveData()

    private val _bleDevices = MutableLiveData<List<BleMeshAdvertiseData>>()
    val bleDevices: LiveData<List<BleMeshAdvertiseData>> = _bleDevices

    init {
        bleDeviceMap.observeForever { deviceMap ->
            val currentDevices = _bleDevices.value.orEmpty().associateBy { it.deviceId }
            _bleDevices.value = deviceMap.map { (deviceId, deviceName) ->
                currentDevices[deviceId]?.copy(name = deviceName) ?: BleMeshAdvertiseData(deviceId, deviceName, R.drawable.baseline_person_24, false)
            }
        }
    }


//    init {

//        bleDeviceMap.observeForever { deviceMap ->
//            _bleDevices.value = deviceMap.map { (deviceId, deviceName) ->
//                BleMeshAdvertiseData(deviceId, deviceName, R.drawable.baseline_person_24, false)
//            }
//        }
//        bleDeviceNames.observeForever { names ->
//            //현재 상태 유지
//            //_bleDevices.value에서 현재 장치들의 상태를 associateBy { it.name }를 사용하여 이름을 키로 하는 맵으로 변환
//            val currentDevices = _bleDevices.value.orEmpty().associateBy { it.name }
//            //bleDeviceNames의 새로운 이름 목록에 대해 BleMeshAdvertiseData 객체를 생성할 때, 기존에 존재하는 장치의 상태를 유지
//            //즉, 이름 목록에 있는 각 이름에 대해 이미 _bleDevices에 존재하는 장치인 경우 그 장치의 isRequestingBle 상태를 그대로 유지
//            //새로운 이름이 기존 맵에 존재하지 않는 경우에만 새로운 BleMeshAdvertiseData 객체를 생성. 이는 새로 추가된 장치에 대한 처리
//            _bleDevices.value = names.map { name ->
//                currentDevices[name] ?: BleMeshAdvertiseData(name, R.drawable.baseline_person_24, false)
//            }
//        }
//    }


    // BLE 장치 상태를 업데이트하는 함수입니다.
    fun updateBleDeviceState(deviceId: String, isRequesting: Boolean) {
        val currentList = _bleDevices.value ?: listOf()
        val updatedList = currentList.map { device ->
            if (device.deviceId == deviceId) device.copy(isRequestingBle = isRequesting) else device
        }
        _bleDevices.value = updatedList
    }


//    fun updateBleDeviceState(deviceName: String, isRequesting: Boolean) {
//        val currentList = _bleDevices.value ?: listOf()
//        val updatedList = currentList.map { device ->
//            if (device.name == deviceName) device.copy(isRequestingBle = isRequesting) else device
//        }
//        _bleDevices.value = updatedList
//    }




}