package com.saveurlife.goodnews.alert

import android.util.Log
import com.saveurlife.goodnews.models.Alert
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertDatabaseManager {

    //Realm에 알림 저장
    fun createAlarm(alert: Alert, onSuccess: () -> Unit) {
        Log.i("createAlert", "createAlert: $alert")
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(Alert::class)).build()
            val realm = Realm.open(config)

            try {
                val existingMessage = realm.query<Alert>("id = $0", alert.id).first().find()
                if (existingMessage == null) {
                    realm.write {
                        copyToRealm(alert)
                    }
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    Log.i("createChatMessage", "Chat message with id ${alert.id} already exists.")

                    realm.writeBlocking {
                        findLatest(existingMessage)
                            ?.also { delete(it) }
                    }

                    realm.write {
                        copyToRealm(alert)
                    }
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }

//                    realm.write {
//                        existingMessage?.content = alert.content
//                        existingMessage?.latitude = alert.latitude
//                        existingMessage?.longitude = alert.longitude
//                        existingMessage?.time = alert.time
//                    }
                }
            } finally {
                realm.close()
            }
        }
    }


//    fun createAlarm(alert: Alert, function: () -> Unit) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val config = RealmConfiguration.Builder(schema = setOf(Alert::class)).build()
//            val realm = Realm.open(config)
//
//            try {
//                val testId = "bcac980c3b4732fa" // 예시 ID
//                val existingFamilyMemInfo = realm.query<Alert>("id = $0", testId).first().find()
//                if (existingFamilyMemInfo == null) {
//                    realm.write {
//                        // 테스트 데이터 생성
//                        val familyMemInfo = Alert(
//                            id = testId,
//                            senderId = "잉",
//                            name = "이준용",
//                            content = "도와주라",
//                            latitude = 37.7749,
//                            longitude = -122.4194,
//                            time = RealmInstant.from(1622640000, 0), // 예시 날짜
//                        )
//                        copyToRealm(familyMemInfo)
//                    }
//                    withContext(Dispatchers.Main) {
//                        Log.i("createAlert", "Alert created successfully")
//                    }
//                } else {
//                    Log.i("createAlert", "Alert with id $testId already exists.")
//                }
//            } finally {
//                realm.close()
//            }
//        }
//    }

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
