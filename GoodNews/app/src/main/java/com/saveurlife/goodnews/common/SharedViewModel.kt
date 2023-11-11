package com.saveurlife.goodnews.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleMeshAdvertiseData
import com.saveurlife.goodnews.ble.service.BleService

class SharedViewModel : ViewModel() {
    val isOnFlash: MutableLiveData<Boolean> = MutableLiveData(false)

    val bleDeviceMap: MutableLiveData<Map<String, String>> = MutableLiveData()

    private val _bleDevices = MutableLiveData<List<BleMeshAdvertiseData>>()
    val bleDevices: LiveData<List<BleMeshAdvertiseData>> = _bleDevices

    val bleService: MutableLiveData<BleService?> = MutableLiveData()

    init {
        bleDeviceMap.observeForever { deviceMap ->
            val currentDevices = _bleDevices.value.orEmpty().associateBy { it.deviceId }
            _bleDevices.value = deviceMap.map { (deviceId, deviceName) ->
                currentDevices[deviceId]?.copy(name = deviceName) ?: BleMeshAdvertiseData(deviceId, deviceName, R.drawable.baseline_person_24, false)
            }
        }
    }


    // BLE 장치 상태를 업데이트하는 함수입니다.
    fun updateBleDeviceState(deviceId: String, isRequesting: Boolean) {
        bleService.value?.let { service ->
            // BluetoothDevice 객체 얻기
            val device = service.getBluetoothDeviceById(deviceId)
            if (device != null) {
                if (isRequesting) {
                    service.connectToDevice(device)
                } else {
                    service.disconnect(device)
                }
            }
        }

        val currentList = _bleDevices.value ?: listOf()
        val updatedList = currentList.map { device ->
            if (device.deviceId == deviceId) device.copy(isRequestingBle = isRequesting) else device
        }
        _bleDevices.value = updatedList
    }
}