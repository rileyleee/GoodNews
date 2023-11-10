package com.saveurlife.goodnews.chatting

data class OnechattingData(
    var otherName: String,
    var lastChatting: String,
    var date: String,
    var isRead: Boolean = false,
    var otherStatus: String
)
