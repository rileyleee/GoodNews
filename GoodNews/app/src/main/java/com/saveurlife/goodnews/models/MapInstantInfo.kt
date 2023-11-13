package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject

class MapInstantInfo():RealmObject {
    var id:Long=0 //사용자 전화번호 위치 정보 현재 시각 정보 조합
    var state:String="" // 위험 안전에 따라 한 글자로 표현
    var content: String=""
    var time: RealmInstant= RealmInstant.from(0,0)
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(id: Long, state:String, content:String, time:RealmInstant, latitude:Double, longitude:Double):this(){
        this.id = id
        this.state = state
        this.content = content
        this.time = time
        this.latitude = latitude
        this.longitude = longitude
    }
}