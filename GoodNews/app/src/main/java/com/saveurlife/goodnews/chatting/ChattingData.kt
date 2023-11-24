package com.saveurlife.goodnews.chatting

import com.saveurlife.goodnews.models.ChatMessage

enum class ChattingType {
    CHAT, DATE
}
data class ChattingData(
    val type: ChattingType? = null,
    val content: String,
    val chatMessage: ChatMessage? = null
)
