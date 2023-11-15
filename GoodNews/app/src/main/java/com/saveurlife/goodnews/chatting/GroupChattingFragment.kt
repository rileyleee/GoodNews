package com.saveurlife.goodnews.chatting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentGroupChattingBinding
import com.saveurlife.goodnews.databinding.FragmentOneChattingBinding

class GroupChattingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {
        var binding = FragmentGroupChattingBinding.inflate(inflater, container, false)

        val chatting = listOf(
            OnechattingData("김싸피, 이싸피", "갈게", "오후 10:26", false, "safe"),
            OnechattingData("이싸피, 김싸피", "안녕", "오후 1:04", false, "injury"),
            OnechattingData("선싸피, 박싸피", "어디야", "오전 10:22", true,"death"),
            OnechattingData("신싸피, 정싸피", "안녕하세요", "2023-11-07", false, "unknown"),
            OnechattingData("박싸피, 민싸피", "조심해", "2023-11-06", true, "safe"),
        )

        val adapter = GroupChattingAdapter(chatting)
        val recyclerView = binding.recyclerViewChatting
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter


        return binding.root
    }
}