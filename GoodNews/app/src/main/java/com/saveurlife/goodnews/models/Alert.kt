package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Alert(): RealmObject {
    @PrimaryKey
    var id: String = ""
    var senderId: String = ""
    var name: String = ""
    var content: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var time: RealmInstant = RealmInstant.from(0,0)
    var type: String = ""

    constructor(id: String, senderId: String, name: String, content: String, latitude: Double, longitude: Double, time: RealmInstant, type: String): this(){
        this.id = id
        this.senderId = senderId
        this.name = name
        this.content = content
        this.latitude = latitude
        this.longitude = longitude
        this.time = time
        this.type = type
    }
}