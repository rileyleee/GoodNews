package com.saveurlife.goodnews.ble

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.saveurlife.goodnews.ble.message.ChatDatabaseManager
import com.saveurlife.goodnews.models.ChatMessage

class ChatRepository(private val chatDatabaseManager: ChatDatabaseManager) {

    private val chatRoomMessagesLiveData = mutableMapOf<String, MutableLiveData<List<ChatMessage>>>()


    fun getChatRoomMessages(chatRoomId: String): MutableLiveData<List<ChatMessage>> {
        val liveData = chatRoomMessagesLiveData.getOrPut(chatRoomId) {
            MutableLiveData<List<ChatMessage>>()
        }
        chatDatabaseManager.getChatMessagesForChatRoom(chatRoomId) { messages ->
            liveData.postValue(messages)
            // 로그로 메시지 출력
            messages.forEach { message ->
                Log.d("그냥메시지", "Message: ${message.content}, IsRead: ${message.isRead}")
            }
        }
        return liveData
    }

    fun getReadUpdatedChatRoomMessages(chatRoomId: String): MutableLiveData<List<ChatMessage>> {
        val liveData = chatRoomMessagesLiveData.getOrPut(chatRoomId) {
            MutableLiveData<List<ChatMessage>>()
        }
        // '읽음' 상태를 업데이트하고 채팅 메시지를 가져오는 로직
        chatDatabaseManager.updateIsReadStatus(chatRoomId) {
            chatDatabaseManager.getChatMessagesForChatRoom(chatRoomId) { messages ->
                liveData.postValue(messages)
                // 로그로 메시지 출력
                messages.forEach { message ->
                    Log.d("읽음처리메시지", "Message: ${message.content}, IsRead: ${message.isRead}")
                }
            }
        }
        return liveData
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
            getReadUpdatedChatRoomMessages(chatRoomId)
            // 업데이트가 성공했을 때 수행할 작업을 여기에 추가하십시오.
            Log.d("ChatRepository", "isRead status updated for chatRoomId: $chatRoomId")
        }
    }
}