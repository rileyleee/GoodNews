//package com.saveurlife.goodnews.models
//
//import io.realm.kotlin.types.RealmInstant
//import io.realm.kotlin.types.RealmObject
//import io.realm.kotlin.types.annotations.PrimaryKey
//
//class GroupMemInfo():RealmObject {
//    @PrimaryKey
//    var id: String=""
//    var groupId: String = "" // 위치 좌표와 전화번호 현재시각 조합하여 활용
//    var groupName: String = ""
//    var name: String = ""
//    var lastConnection: RealmInstant = RealmInstant.from(0,0)
//    var state: String = ""
//    var latitude: Double = 0.0
//    var longitude: Double = 0.0
//
//    constructor(id: Int, name:String, groupId: Long, lastConnection:RealmInstant, state:String, latitude: Double, longitude: Double): this(){
//        this.id = id
//        this.name = name
//        this.groupId = groupId
//        this.groupName = groupName
//        this.time = time
//        this.state = state
//        this.latitude = latitude
//        this.longitude = longitude
//    }
//}