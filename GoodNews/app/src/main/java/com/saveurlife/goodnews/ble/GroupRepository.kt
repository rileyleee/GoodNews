//package com.saveurlife.goodnews.ble
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.saveurlife.goodnews.ble.message.ChatDatabaseManager
//import com.saveurlife.goodnews.ble.message.GroupDatabaseManager
//import com.saveurlife.goodnews.models.Chat
//import com.saveurlife.goodnews.models.ChatMessage
//import com.saveurlife.goodnews.models.GroupMemInfo
//import io.realm.kotlin.Realm
//import io.realm.kotlin.RealmConfiguration
//import io.realm.kotlin.ext.query
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class GroupRepository(private val groupDatabaseManager: GroupDatabaseManager) {
//
//    private val groupMembersLiveData = mutableMapOf<String, MutableLiveData<List<GroupMemInfo>>>()
//
//    fun getGroupMembers(groupId: String): MutableLiveData<List<GroupMemInfo>>{
//        return groupMembersLiveData.getOrPut(groupId){
//            MutableLiveData<List<GroupMemInfo>>().apply {
//                groupDatabaseManager.getGroupMembersForGroup(groupId){ members ->
//                    postValue(members)
//                }
//            }
//        }
//    }
//
//    fun getAllGroupIds(): LiveData<List<String>> {
//        val groupIdsLiveData = MutableLiveData<List<String>>()
//
//        groupDatabaseManager.getAllGroupIds { groupIds ->
//            groupIdsLiveData.postValue(groupIds)
//
//        }
//        return groupIdsLiveData
//    }
//
//
//
////    fun addMembersToGroup(groupId: String, groupName: String, members: List<BleMeshConnectedUser>) {
////        val groupMemInfoList = members.map { member ->
////            GroupMemInfo(
////                id = member.userId,
////                groupId = groupId,
////                groupName = groupName,
////                name = member.userName,
////                time = member.updateTime,
////                state = member.healthStatus,
////                latitude = member.lat,
////                longitude = member.lon
////
////            )
////        }
////
////        for (groupMemInfo in groupMemInfoList) {
////            groupDatabaseManager.createGroupMember(groupId, groupMemInfo) {
////                // 원하는 경우 여기에서 작업을 수행할 수 있습니다.
////            }
////        }
////
////        updateGroupMembersLiveData(groupId, groupMemInfoList)
////    }
//
//    private fun updateGroupMembersLiveData(groupId: String, newMemInfoList: List<GroupMemInfo>) {
//        val currentList = groupMembersLiveData[groupId]?.value ?: emptyList()
//        groupMembersLiveData[groupId]?.postValue(currentList + newMemInfoList)
//    }
//
//
//
//
//
//
//
//
////    fun addMembersToGroup(groupId: String, groupName: String, members: List<BleMeshConnectedUser>){
////        for(member in members){
////            var groupMemInfo = GroupMemInfo(id = member.userId, groupId = groupId, groupName = groupName, name = member.userName, time = member.updateTime, state = member.healthStatus, latitude = member.lat, longitude = member.lon)
////            groupDatabaseManager.createGroupMember(groupId, groupMemInfo){
////                updateGroupMembersLiveData(groupId, groupMemInfo)
////            }
////        }
////    }
////
////    private fun updateGroupMembersLiveData(groupId: String, newMemInfo: GroupMemInfo){
////        val currentList = groupMembersLiveData[groupId]?.value ?: emptyList()
////        groupMembersLiveData[groupId]?.postValue(currentList + newMemInfo)
////    }
//
//
//
//
//
//
//
//
//    fun getAllGroupIds(onSuccess: (List<String>) -> Unit){
//        CoroutineScope(Dispatchers.IO).launch {
//            val config = RealmConfiguration.Builder(schema = setOf(Chat::class, ChatMessage::class)).build()
//            val realm = Realm.open(config)
//
//            try {
//                // 모든 Chat 객체를 가져와서 id를 추출
//                val chatIds = realm.query<Chat>()
//                    .find()
//                    .map { it.id }
//                    .distinct()
//
//                withContext(Dispatchers.Main) {
//                    onSuccess(chatIds)
//                }
//            } catch (e: Exception) {
//                Log.e("ChatDatabaseManager", "Error fetching chat IDs", e)
//                // 오류 처리를 위한 추가적인 로직 (옵션)
//            } finally {
//                realm.close()
//            }
//        }
//    }
//}
