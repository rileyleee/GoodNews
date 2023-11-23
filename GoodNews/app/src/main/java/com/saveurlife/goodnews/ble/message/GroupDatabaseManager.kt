package com.saveurlife.goodnews.ble.message

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmList
import androidx.lifecycle.MutableLiveData
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.models.Chat
import com.saveurlife.goodnews.models.ChatMessage
import com.saveurlife.goodnews.models.GroupMemInfo
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupDatabaseManager {

    fun createGroupMember(groupId: String, groupMemInfo: GroupMemInfo, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(GroupMemInfo::class)).build()
            val realm = Realm.open(config)

            try {
                val existingGroup = realm.query<GroupMemInfo>("groupId = $0", groupId).first().find()
                if (existingGroup == null) {
                    realm.write {
                        copyToRealm(groupMemInfo)
                    }
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    Log.i("createGroupMember", "Group member with groupId $groupId already exists.")
                }
            } finally {
                realm.close()
            }
        }
    }





    fun getAllGroupIds(onSuccess: (List<String>) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(GroupMemInfo::class)).build()
            val realm = Realm.open(config)

            try {
                // 모든 메시지를 가져와서 chatRoomId를 추출
                val groupIds = realm.query<GroupMemInfo>()
                    .distinct("groupId")
                    .find()
                    .map { it.groupId }
                    .distinct()

                withContext(Dispatchers.Main) {
                    onSuccess(groupIds)
                }
            } finally {
                realm.close()
            }
        }
    }




    fun getGroupMembersForGroup(groupId: String, onSuccess: (List<GroupMemInfo>) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val config = RealmConfiguration.Builder(schema = setOf(GroupMemInfo::class)).build()
            val realm = Realm.open(config)

            try {
                val members = realm.query<GroupMemInfo>("groupId = $0", groupId)
                    .find()
                    .toList() // 결과를 List로 변환

                // Realm 객체를 일반 Kotlin 객체로 변환
                val membersCopy = members.map { groupMapInfo ->
                    GroupMemInfo(
                        id = groupMapInfo.id,
                        groupId = groupMapInfo.groupId,
                        groupName = groupMapInfo.groupName,
                        userId = groupMapInfo.userId,
                        userName = groupMapInfo.userName
                    )
                }

                withContext(Dispatchers.Main) {
                    onSuccess(membersCopy)
                }
            } finally {
                realm.close()
            }
        }
    }

}