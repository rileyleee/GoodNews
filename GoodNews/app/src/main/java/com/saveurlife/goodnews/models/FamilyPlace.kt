package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class FamilyPlace(): RealmObject {
    @PrimaryKey
    var placeId:Int=0
    var name: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var canUse: Boolean = true

    constructor(placeId:Int, name:String, latitude: Double, longitude: Double, canUse:Boolean):this(){
        this.placeId = placeId
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
        this.canUse = canUse
    }

}