package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject

class MapInstantInfo():RealmObject {
    var state: String= "" // 위험 O 안전에 따라 한 글자로 표현
    var content: String=""
    var time: RealmInstant= RealmInstant.from(0,0)
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(state:String, content:String, time:RealmInstant, latitude:Double, longitude:Double):this(){
        this.state = state
        this.content = content
        this.time = time
        this.latitude = latitude
        this.longitude = longitude
    }
}