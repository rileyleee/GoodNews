package com.saveurlife.goodnews.models

//import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class OffMapFacility : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var type: String = ""
    var name: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var canUse: Boolean = false
    var addInfo: String = ""
    // realmList null 안된다고 오류 떠서 주석처리
    //var aidKitList: RealmList<AidKit>? = null


    var enumType: FacilityType
        get() = FacilityType.values().first { it.type == type }
        set(value) {
            type = value.type
        }
}