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
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.FragmentOneChattingBinding

class OneChattingFragment : Fragment() {
    val sharedViewModel: SharedViewModel by activityViewModels()
    private val chatDataList = mutableListOf<OnechattingData>()

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {
        var binding = FragmentOneChattingBinding.inflate(inflater, container, false)

        val adapter = OneChattingAdapter(chatDataList)
        val recyclerView = binding.recyclerViewChatting
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        loadChatRooms(adapter)


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

}