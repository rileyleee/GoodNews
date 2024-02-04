package com.saveurlife.goodnews.ble.message

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import com.saveurlife.goodnews.models.ChatMessage
import com.saveurlife.goodnews.models.FamilyMemInfo
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatDatabaseManager {
    fun createFamilyMemInfo() {
        Log.i("createFamilyMemInfo", "Creating FamilyMemInfo with hardcoded data")
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(FamilyMemInfo::class)).build()
            val realm = Realm.open(config)

            try {
                val testId = "bcac980c3b4732fa" // 예시 ID
                val existingFamilyMemInfo = realm.query<FamilyMemInfo>("id = $0", testId).first().find()
                if (existingFamilyMemInfo == null) {
                    realm.write {
                        // 테스트 데이터 생성
                        val familyMemInfo = FamilyMemInfo(
                            id = testId,
                            name = "이준용",
                            phone = "123-4566-7890",
                            lastConnection = RealmInstant.from(1622640000, 0), // 예시 날짜
                            state = "safe",
                            latitude = 37.7749,
                            longitude = -122.4194,
                            familyId = "family123"
                        )
                        copyToRealm(familyMemInfo)
                    }
                    withContext(Dispatchers.Main) {
                        Log.i("createFamilyMemInfo", "FamilyMemInfo created successfully")
                    }
                } else {
                    Log.i("createFamilyMemInfo", "FamilyMemInfo with id $testId already exists.")
                }
            } finally {
                realm.close()
            }
        }
    }









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
        Log.i("getChatMessagesForChatRoom", "getChatMessagesForChatRoom: ")
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
        Log.i("updateIsReadStatus", "updateIsReadStatus: ")
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(ChatMessage::class)).build()
            val realm = Realm.open(config)

            try {
                // 쿼리를 먼저 실행하고 결과를 리스트로 저장
                val messagesToUpdate = realm.query<ChatMessage>("chatRoomId = $0", chatRoomId)
                    .find()
                    .toList()

                // 쓰기 트랜잭션을 시작하여 메시지의 isRead 값을 업데이트
                realm.write {
                    messagesToUpdate.forEach { message ->
                        findLatest(message)?.let { it.isRead = true }
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