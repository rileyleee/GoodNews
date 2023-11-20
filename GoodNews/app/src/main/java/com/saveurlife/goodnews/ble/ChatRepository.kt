package com.saveurlife.goodnews.ble

import android.util.Log
import androidx.lifecycle.LiveData
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

    fun getAllChatRoomIds(): LiveData<List<String>> {
        val chatRoomIdsLiveData = MutableLiveData<List<String>>()

        chatDatabaseManager.getAllChatRoomIds { chatRoomIds ->
            chatRoomIdsLiveData.postValue(chatRoomIds)
        }

        return chatRoomIdsLiveData
    }


    fun addMessageToChatRoom(chatRoomId: String, chatRoomName: String, senderId: String, senderName: String, content: String, time: String, isRead: Boolean) {
        val chatMessage = ChatMessage(id = senderId+time, chatRoomId = chatRoomId, chatRoomName = chatRoomName, senderId = senderId, senderName = senderName, content = content, time = time, isRead = isRead)
        chatDatabaseManager.createChatMessage(chatRoomId, chatMessage) {
            updateChatRoomMessagesLiveData(chatRoomId, chatMessage)
        }
        println("$chatRoomId $chatRoomName $senderId  $senderName $content $time $isRead 무슨 메세지야 ?????")
    }

    private fun updateChatRoomMessagesLiveData(chatRoomId: String, newMessage: ChatMessage) {
        val currentList = chatRoomMessagesLiveData[chatRoomId]?.value ?: emptyList()
        chatRoomMessagesLiveData[chatRoomId]?.postValue(currentList + newMessage)
    }

    fun updateIsReadStatus(chatRoomId: String) {
        chatDatabaseManager.updateIsReadStatus(chatRoomId) {
            // 업데이트가 성공했을 때 수행할 작업을 여기에 추가하십시오.
            Log.d("ChatRepository", "isRead status updated for chatRoomId: $chatRoomId")
        }
    }
}