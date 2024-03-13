package com.saveurlife.goodnews.chatting

import MainAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentChattingBinding

class ChattingFragment : Fragment(R.layout.fragment_chatting) {
    private lateinit var binding: FragmentChattingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChattingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val tabs: TabLayout = view.findViewById(R.id.chattingTabs)
        val viewPager: ViewPager2 = view.findViewById(R.id.chattingViewPager)

        val adapter = ChattingAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabs,  viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "채팅"
//                1 -> "그룹"
                else -> null
            }
        }.attach()

        val selectedTab = arguments?.getInt("selectedTab") ?: 0

        //TabLayoutMediator의 attach 호출 후에 currentItem을 설정
        viewPager.post {
            viewPager.currentItem = selectedTab
        }
    }
}