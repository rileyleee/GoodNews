package com.saveurlife.goodnews.chatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.FragmentOneChattingBinding

class OneChattingFragment : Fragment() {
    val sharedViewModel: SharedViewModel by activityViewModels()
    private val chatDataList = mutableListOf<OnechattingData>()
    private lateinit var users: List<BleMeshConnectedUser>
    private lateinit var adapter : OneChattingAdapter

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {

        var binding = FragmentOneChattingBinding.inflate(inflater, container, false)

        // 연결된 사용자 리스트 가져오기
        getChattingListsWhenConnected(binding)

        return binding.root
    }

    private fun loadChatRooms(adapter: OneChattingAdapter) {

        sharedViewModel.bleService.value?.let { bleService ->
            bleService.allChatRoomIds.observe(viewLifecycleOwner, Observer { chatRoomIds ->
                Log.i("check", chatRoomIds.toString())
                chatRoomIds?.forEach { chatRoomId ->
                    Log.i("챗룸아이디", chatRoomId)
                    bleService.getChatRoomMessages(chatRoomId).observe(viewLifecycleOwner) { messages ->
                        if (messages.isNotEmpty()) {
                            val lastMessage = messages.last()
                            val user = bleService.getBleMeshConnectedUser(lastMessage.chatRoomId)

                            val newChatData = OnechattingData(
                                chatRoomId = lastMessage.chatRoomId,
                                otherName = lastMessage.chatRoomName,
                                lastChatting = lastMessage.content,
                                date = lastMessage.time,
                                isRead = lastMessage.isRead,
                                otherStatus = user?.healthStatus ?: "unknown"
                            )
                            updateChatDataList(chatRoomId, newChatData, adapter)
                        }
                    }
                }
            })
        }
    }

    private fun updateChatDataList(chatRoomId: String, newChatData: OnechattingData, adapter: OneChattingAdapter) {
        val existingIndex = chatDataList.indexOfFirst { it.chatRoomId == chatRoomId }
        if (existingIndex != -1) {
            // 채팅방 아이템이 이미 존재하면 업데이트
            chatDataList[existingIndex] = newChatData
        } else {
            // 새 채팅방 아이템 추가
            chatDataList.add(newChatData)
        }
        adapter.notifyDataSetChanged()
    }


    private fun updateChatDataList(chatData: OnechattingData, adapter: OneChattingAdapter) {
        // 채팅 데이터 리스트 업데이트 및 어댑터에 반영
        chatDataList.add(chatData)
        adapter.notifyDataSetChanged()
    }

    private fun getChattingListsWhenConnected(binding: FragmentOneChattingBinding) {

        // BLE로 연결된 이용자가 없으면 작동하지 않음
        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(
            viewLifecycleOwner,
            Observer { connectedDevicesMap ->
                Log.v("OneChattingFragment","옵저버 가넝~")
                // 중첩된 맵에서 BleMeshConnectedUser 추출
                users = connectedDevicesMap.flatMap { it.value.values }.toList()
                Log.v("연결된 사용자 수: ", users.size.toString())
                adapter = OneChattingAdapter(chatDataList, users)
                setupRecyclerView(binding)
                loadChatRooms(adapter)

            })
        // 그래서 여기서 어댑터가 초기화 되지 않기 때문에 터짐
        // Log.v("없다 있다 어떻게 구분",adapter.toString())
    }

    private fun setupRecyclerView(binding: FragmentOneChattingBinding) {
        val recyclerView = binding.recyclerViewChatting
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        adapter.listener = object : OneChattingAdapter.OnItemClickListener {
            override fun onItemClick(chatData: OnechattingData) {
                val intent = Intent(context, ChattingDetailActivity::class.java).apply {
                    putExtra("chatRoomId", chatData.chatRoomId)
                    putExtra("chatName", chatData.otherName)
                    putExtra("chatOtherStatus", chatData.otherStatus)
                    putExtra("page",2)
                }
                startActivity(intent)
            }
        }
    }


}