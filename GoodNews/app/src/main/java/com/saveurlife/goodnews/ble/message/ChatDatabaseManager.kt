package com.saveurlife.goodnews.ble.message

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmList
import androidx.lifecycle.MutableLiveData
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.models.ChatMessage
import io.realm.kotlin.query.Sort
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
                realm.write {
                    copyToRealm(chatMessage)
                }
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } finally {
                realm.close()
            }
        }
    }

//    fun getChatMessagesForChatRoom(chatRoomId: String, onSuccess: (List<ChatMessage>) -> Unit) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val config = RealmConfiguration.Builder(schema = setOf(ChatMessage::class)).build()
//            val realm = Realm.open(config)
//
//            try {
//                // Query the database for messages belonging to the specified chat room.
//                val messages = realm.query<ChatMessage>("chatRoomId = $0", chatRoomId)
//                    .find()
//                    .toList() // Convert results to a List.
//
//                withContext(Dispatchers.Main) {
//                    onSuccess(messages)
//                }
//            } finally {
//                realm.close()
//            }
//        }
//    }

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
                        senderId = chatMessage.senderId,
                        senderName = chatMessage.senderName,
                        content = chatMessage.content,
                        time = chatMessage.time
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

}