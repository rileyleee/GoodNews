package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class FamilyPlace(): RealmObject {
    @PrimaryKey
    var id:Int=0
    var familyId: Long=0
    var name: String = ""
    var location: Location?= null
    var canUse: Boolean = true

    constructor(id:Int, familyId: Long, name:String, location: Location, canUse:Boolean):this(){
        this.id = id
        this.familyId = familyId
        this.name = name
        this.location = location
        this.canUse = canUse
    }

}