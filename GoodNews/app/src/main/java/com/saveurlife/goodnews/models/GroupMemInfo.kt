package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class GroupMemInfo():RealmObject {
    @PrimaryKey
    var id: String="" //userId+datetime
    var groupId: String = "" // 위치 좌표와 전화번호 현재시각 조합하여 활용
    var groupName: String = ""
    var userId: String =""
    var userName: String = ""


    constructor(id: String, groupId: String, groupName: String, userId: String, userName: String): this(){
        this.id = id
        this.groupId = groupId
        this.groupName = groupName
        this.userId = userId
        this.userName = userName
    }
}