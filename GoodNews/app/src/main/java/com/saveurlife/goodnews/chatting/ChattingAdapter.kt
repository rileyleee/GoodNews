package com.saveurlife.goodnews.chatting

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChattingAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OneChattingFragment()
//            1 -> GroupChattingFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}