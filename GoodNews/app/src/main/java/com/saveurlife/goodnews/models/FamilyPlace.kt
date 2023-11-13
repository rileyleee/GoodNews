package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class FamilyPlace(): RealmObject {
    @PrimaryKey
    var placeId:Int=0
    var name: String = ""
    var location: Location?= null
    var canUse: Boolean = true

    constructor(placeId:Int, name:String, location: Location, canUse:Boolean):this(){
        this.placeId = placeId
        this.name = name
        this.location = location
        this.canUse = canUse
    }

}