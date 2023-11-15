package com.saveurlife.goodnews.ble

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.saveurlife.goodnews.ble.message.ChatDatabaseManager
import com.saveurlife.goodnews.models.ChatMessage

class ChatRepository(private val chatDatabaseManager: ChatDatabaseManager) {

    private val chatRoomMessagesLiveData = mutableMapOf<String, MutableLiveData<List<ChatMessage>>>()

    fun getChatRoomMessages(chatRoomId: String): MutableLiveData<List<ChatMessage>> {
        return chatRoomMessagesLiveData.getOrPut(chatRoomId) {
            MutableLiveData<List<ChatMessage>>().apply {
                chatDatabaseManager.getChatMessagesForChatRoom(chatRoomId) { messages ->
                    postValue(messages)
                }
            }
        }
    }


//    fun addMessageToChatRoom(chatRoomId: String, chatMessage: ChatMessage) {
//        chatDatabaseManager.createChatMessage(chatRoomId, chatMessage) {
//            updateChatRoomMessagesLiveData(chatRoomId, chatMessage)
//        }
//    }

    fun addMessageToChatRoom(chatRoomId: String, senderId: String, senderName: String, content: String, time: String) {
        val chatMessage = ChatMessage(id = senderId+time, chatRoomId = chatRoomId, senderId = senderId, senderName = senderName, content = content, time = time)
        println()
        chatDatabaseManager.createChatMessage(chatRoomId, chatMessage) {
            updateChatRoomMessagesLiveData(chatRoomId, chatMessage)
        }
        println("$chatRoomId $senderId  $senderName $content $time 무슨 메세지야 ?????")
    }

    private fun updateChatRoomMessagesLiveData(chatRoomId: String, newMessage: ChatMessage) {
        val currentList = chatRoomMessagesLiveData[chatRoomId]?.value ?: emptyList()
        chatRoomMessagesLiveData[chatRoomId]?.postValue(currentList + newMessage)
    }
}
