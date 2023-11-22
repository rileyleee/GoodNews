package com.saveurlife.goodnews.ble.message

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import com.saveurlife.goodnews.models.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatDatabaseManager {

    fun createChatMessage(chatRoomId: String, chatMessage: ChatMessage, onSuccess: () -> Unit) {
        Log.i("createChatMessage", "createChatMessage: ")
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(ChatMessage::class)).build()
            val realm = Realm.open(config)

            try {
                val existingMessage = realm.query<ChatMessage>("id = $0", chatMessage.id).first().find()
                if (existingMessage == null) {
                    realm.write {
                        copyToRealm(chatMessage)
                    }
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    Log.i("createChatMessage", "Chat message with id ${chatMessage.id} already exists.")
                }
            } finally {
                realm.close()
            }
        }
    }


    fun getAllChatRoomIds(onSuccess: (List<String>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(ChatMessage::class)).build()
            val realm = Realm.open(config)

            try {
                // 모든 메시지를 가져와서 chatRoomId를 추출
                val chatRoomIds = realm.query<ChatMessage>()
                    .distinct("chatRoomId")
                    .find()
                    .map { it.chatRoomId }
                    .distinct()

                withContext(Dispatchers.Main) {
                    onSuccess(chatRoomIds)
                }
            } finally {
                realm.close()
            }
        }
    }


    fun getChatMessagesForChatRoom(chatRoomId: String, onSuccess: (List<ChatMessage>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(ChatMessage::class)).build()
            val realm = Realm.open(config)

            try {
                val messages = realm.query<ChatMessage>("chatRoomId = $0", chatRoomId)
                    .find()
                    .toList() // 결과를 List로 변환

                // Realm 객체를 일반 Kotlin 객체로 변환
                val messagesCopy = messages.map { chatMessage ->
                    ChatMessage(
                        id = chatMessage.id,
                        chatRoomId = chatMessage.chatRoomId,
                        chatRoomName = chatMessage.chatRoomName,
                        senderId = chatMessage.senderId,
                        senderName = chatMessage.senderName,
                        content = chatMessage.content,
                        time = chatMessage.time,
                        isRead = chatMessage.isRead
                    )
                }

                withContext(Dispatchers.Main) {
                    onSuccess(messagesCopy)
                }
            } finally {
                realm.close()
            }
        }
    }



    fun updateIsReadStatus(chatRoomId: String, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(ChatMessage::class)).build()
            val realm = Realm.open(config)

            try {
                // 주어진 chatRoomId와 일치하는 모든 메시지의 isRead 값을 true로 업데이트
                realm.write {
                    val messagesToUpdate = realm.query<ChatMessage>("chatRoomId = $0", chatRoomId)
                        .find()
                        .toList()

                    messagesToUpdate.forEach { message ->
                        message.isRead = true
                    }
                }

                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } finally {
                realm.close()
            }
        }
    }
}