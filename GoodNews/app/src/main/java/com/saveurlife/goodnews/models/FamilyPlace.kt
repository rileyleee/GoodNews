package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class FamilyPlace(): RealmObject {
    @PrimaryKey
    var id:Int=0
    var familyId: Long=0
    var name: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var canUse: Boolean = true

    constructor(id:Int, familyId: Long, name:String, latitude: Double, longitude: Double, canUse:Boolean):this(){
        this.id = id
        this.familyId = familyId
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
        this.canUse = canUse
    }

}