package com.saveurlife.goodnews.flashlight

enum class FlashType {
    SELF, OTHER
}

data class FlashlightData(
    val type: FlashType? = null,
    val content: String
)
