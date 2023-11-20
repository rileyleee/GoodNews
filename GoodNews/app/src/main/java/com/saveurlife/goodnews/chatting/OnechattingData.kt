package com.saveurlife.goodnews.chatting

data class OnechattingData(
    var chatRoomId: String,
    var otherName: String,
    var lastChatting: String,
    var date: String,
    var isRead: Boolean = false,
    var otherStatus: String
)