package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ChatMessage() : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var memberId: Long = 0
    var content: String = ""
    var time: RealmInstant = RealmInstant.from(0, 0)

    constructor(id:Int, memberId: Long, content: String, time:RealmInstant):this(){
        this.id= id
        this.memberId = memberId
        this.content = content
        this.time = time
    }
}