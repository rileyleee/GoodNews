package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class FamilyMemInfo(): RealmObject {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var memberId: Long = 0
    var lastConnection: RealmInstant = RealmInstant.from(0,0)
    var state: Char = '0'
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var familyId: Long = 0

    constructor(id: Int, name:String, memberId: Long, lastConnection:RealmInstant, state:Char, latitude: Double, longitude: Double, familyId: Long): this(){
        this.id = id
        this.name = name
        this.memberId = memberId
        this.lastConnection = lastConnection
        this.state = state
        this.latitude = latitude
        this.longitude = longitude
        this.familyId = familyId
    }
}