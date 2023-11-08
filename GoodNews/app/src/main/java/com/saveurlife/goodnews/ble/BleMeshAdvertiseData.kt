package com.saveurlife.goodnews.ble

data class BleMeshAdvertiseData(
    val name: String,
    val img: Int,
    var isRequestingBle: Boolean = false
)
