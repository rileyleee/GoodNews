package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject

class Location() : RealmObject {
    var time: RealmInstant = RealmInstant.from(0,0)
    var longitude: Double = 0.0
    var latitude: Double =0.0
    constructor( time: RealmInstant, lng: Double=0.0, lat:Double=0.0):this(){
        this.time = time
        this.longitude = lng
        this.latitude = lat
    }
}