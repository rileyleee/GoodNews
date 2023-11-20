package com.saveurlife.goodnews.alarm

import android.util.Log
import com.saveurlife.goodnews.models.Alert
import com.saveurlife.goodnews.models.ChatMessage
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmDatabaseManger {

    fun createAlarm(chatRoomId: String, alert: Alert, onSuccess: () -> Unit) {
        Log.i("createChatMessage", "createChatMessage: ")
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(Alert::class)).build()
            val realm = Realm.open(config)

            try {
                realm.write {
                    copyToRealm(alert)
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
//                val messages = realm.query<ChatMessage>("chatRoomId = $0", chatRoomId)
//                    .find()
//                    .toList() // 결과를 List로 변환
//
//                // Realm 객체를 일반 Kotlin 객체로 변환
//                val messagesCopy = messages.map { chatMessage ->
//                    ChatMessage(
//                        id = chatMessage.id,
//                        chatRoomId = chatMessage.chatRoomId,
//                        chatMessage = chatMessage.chatRoomName,
//                        senderId = chatMessage.senderId,
//                        senderName = chatMessage.senderName,
//                        content = chatMessage.content,
//                        time = chatMessage.time
//                    )
//                }
//
//                withContext(Dispatchers.Main) {
//                    onSuccess(messagesCopy)
//                }
//            } finally {
//                realm.close()
//            }
//        }
//    }
}