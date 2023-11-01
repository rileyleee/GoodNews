package com.saveurlife.goodnews.models

import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Chat() : RealmObject {
    @PrimaryKey
    var id: Long = 0
    var name: String = ""
    var messageList: RealmList<ChatMessage>? = null
}

// 객체의 초기화를 생성자에 의존하지 않고
// Realm 트랜잭션 내에서 직접 수행하는 것이
// Realm과 함께 작업할 때 더 안정적이며,
// 특히 RealmList와 같은 특별한 데이터 타입을 사용할 때 오류를 줄일 수 있다.