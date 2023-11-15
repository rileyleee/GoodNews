package com.saveurlife.goodnews.ble

data class BleMeshAdvertiseData(
    val deviceId: String,
    val name: String,
    val img: Int,
    var isRequestingBle: Boolean = false
)
