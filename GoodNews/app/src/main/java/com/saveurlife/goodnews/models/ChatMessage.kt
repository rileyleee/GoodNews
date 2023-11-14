package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.Instant

class ChatMessage() : RealmObject {
    @PrimaryKey
    var id: String = ""
    var chatRoomId : String = ""
    var senderId: String = ""
    var senderName: String = ""
    var content: String = ""
    var time: String =""

    constructor(id: String, chatRoomId: String, senderId: String, senderName: String, content: String, time: String) : this() {
        this.id = id
        this.chatRoomId = chatRoomId
        this.senderId = senderId
        this.senderName = senderName
        this.content = content
        this.time = time
    }
}
