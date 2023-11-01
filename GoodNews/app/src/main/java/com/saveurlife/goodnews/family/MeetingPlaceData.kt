package com.saveurlife.goodnews.family

enum class SafetyStatus {
    SAFE,
    DANGEROUS
}

data class MeetingPlaceData(
    val name: String,
    val address: String,
    val status: SafetyStatus
)
