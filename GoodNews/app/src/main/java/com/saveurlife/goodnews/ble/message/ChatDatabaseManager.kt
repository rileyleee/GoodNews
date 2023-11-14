package com.saveurlife.goodnews.ble.message

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmList
import androidx.lifecycle.MutableLiveData
import com.saveurlife.goodnews.models.ChatMessage
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatDatabaseManager {
    fun createChatMessage(chatRoomId: String, senderId: String, senderName: String, content: String, time: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(ChatMessage::class)).build()
            val realm = Realm.open(config)

            try {
                realm.write {
                    // 새로운 ChatMessage 객체의 ID 생성
                    val newMessageId = "$senderId$time"

                    // 새 ChatMessage 객체 생성
                    val newMessage = ChatMessage(newMessageId, chatRoomId, senderId, senderName, content, time)

                    // 데이터베이스에 객체 추가
                    copyToRealm(newMessage)
                }
            } finally {
                realm.close()
            }
        }
    }

    fun getNowRoomMessageList(chatRoomId: String): List<ChatMessage> {
        val config = RealmConfiguration.Builder(schema = setOf(ChatMessage::class)).build()
        val realm = Realm.open(config)
        val messages: List<ChatMessage>

        try {
            // 특정 채팅방 ID에 해당하는 메시지들을 시간으로 오름차순 정렬하여 검색
            messages = realm.query<ChatMessage>("chatRoomId == $0", chatRoomId)
                .sort("time", Sort.ASCENDING)
                .find()
                .toList() // RealmResults를 List로 변환
        } finally {
            realm.close()
        }
        return messages
    }

}
