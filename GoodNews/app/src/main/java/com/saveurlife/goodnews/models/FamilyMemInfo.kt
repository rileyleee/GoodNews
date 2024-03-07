package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class FamilyMemInfo(): RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var phone: String = ""
    var lastConnection: RealmInstant = RealmInstant.from(0,0)
    var state: String? = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var familyId: Int = 0

    constructor(id: String, name:String, phone: String, lastConnection:RealmInstant, state:String, latitude: Double, longitude: Double, familyId: Int): this(){
        this.id = id
        this.name = name
        this.phone = phone
        this.lastConnection = lastConnection
        this.state = state
        this.latitude = latitude
        this.longitude = longitude
        this.familyId = familyId
    }
}