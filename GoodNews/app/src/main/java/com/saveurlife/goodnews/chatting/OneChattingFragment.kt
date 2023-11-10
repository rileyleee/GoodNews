package com.saveurlife.goodnews.chatting

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.saveurlife.goodnews.databinding.FragmentOneChattingBinding

class OneChattingFragment : Fragment() {
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {
        var binding = FragmentOneChattingBinding.inflate(inflater, container, false)

        val chatting = listOf(
            OnechattingData("김싸피", "갈게", "오후 10:26", false, "safe"),
            OnechattingData("이싸피", "안녕", "오후 1:04", false, "injury"),
            OnechattingData("선싸피", "어디야", "오전 10:22", true,"death"),
            OnechattingData("신싸피", "안녕하세요", "2023-11-07", false, "unknown"),
            OnechattingData("박싸피", "조심해", "2023-11-06", true, "safe")
        )

        val adapter = OneChattingAdapter(chatting)
        val recyclerView = binding.recyclerViewChatting
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter


        adapter.listener = object : OneChattingAdapter.OnItemClickListener {
            override fun onItemClick(chatData: OnechattingData) {
                val intent = Intent(activity, ChattingDetailActivity::class.java)
                intent.putExtra("chatName", chatData.otherName) // 채팅방 이름 전달
                intent.putExtra("chatOtherStatus", chatData.otherStatus)
                startActivity(intent)
            }
        }

        return binding.root
    }

}