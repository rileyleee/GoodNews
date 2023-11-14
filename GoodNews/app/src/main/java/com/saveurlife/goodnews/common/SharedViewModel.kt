package com.saveurlife.goodnews.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleMeshAdvertiseData
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.ble.service.BleService

class SharedViewModel : ViewModel() {
    //플래쉬
    val isOnFlash: MutableLiveData<Boolean> = MutableLiveData(false)

    //bleService
    var bleService: MutableLiveData<BleService?> = MutableLiveData()

    // BLE 장치의 ID와 이름 매핑
    val bleDeviceMap: MutableLiveData<Map<String, String>> = MutableLiveData()

    //BLE 장치들의 리스트
    private val _bleDevices = MutableLiveData<List<BleMeshAdvertiseData>>()

    //캡슐화 : _bleDevices를 감싸는 public LiveData
    val bleDevices: LiveData<List<BleMeshAdvertiseData>> = _bleDevices

    //연결된 BLE 장치
    private val _bleMeshConnectedDevicesMapLiveData = MutableLiveData<Map<String, Map<String, BleMeshConnectedUser>>>()
    val bleMeshConnectedDevicesMapLiveData: LiveData<Map<String, Map<String, BleMeshConnectedUser>>> = _bleMeshConnectedDevicesMapLiveData

    //연결/미연결 ui
    val isMainAroundVisible = MutableLiveData<Boolean>(true)

    //bleDeviceMap의 데이터가 변경될 때마다(새로운 BLE 장치가 발견되거나 기존 장치의 이름이 변경될 때)
    // _bleDevices 리스트를 최신 상태로 유지
    init {
        bleDeviceMap.observeForever { deviceMap ->
            val currentDevices = _bleDevices.value.orEmpty().associateBy { it.deviceId }
            _bleDevices.value = deviceMap.map { (deviceId, deviceName) ->
                currentDevices[deviceId]?.copy(name = deviceName) ?: BleMeshAdvertiseData(deviceId, deviceName, R.drawable.baseline_person_24, false)
            }
        }
    }


    // BLE 장치 상태를 업데이트
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

    //선택된 BLE 장치 업데이트
    fun updateBleMeshConnectedDevicesMap(connectedDevicesMap: Map<String, Map<String, BleMeshConnectedUser>>) {
        _bleMeshConnectedDevicesMapLiveData.value = connectedDevicesMap
    }
}