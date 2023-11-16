package com.saveurlife.goodnews.family

import io.realm.kotlin.types.RealmInstant

// 상태 (건강, 부상, 사망, 표시하지 않음)
enum class Status {
    HEALTHY,
    INJURED,
    DECEASED,
    NOT_SHOWN
}

enum class FamilyType{
    ACCEPT,
    WAIT
}

data class FamilyData(
    val name: String,
    val status: Status = Status.NOT_SHOWN,
    val lastAccessTime: RealmInstant? = null,
    val type: FamilyType = FamilyType.WAIT,
    val acceptNumber:Int = 0
)
