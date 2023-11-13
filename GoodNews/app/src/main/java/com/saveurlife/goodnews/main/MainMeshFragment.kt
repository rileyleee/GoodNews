package com.saveurlife.goodnews.main

import MainAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentMainMeshBinding

class MainMeshFragment() : Fragment(R.layout.fragment_main_mesh) {
    private lateinit var binding: FragmentMainMeshBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainMeshBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabs: TabLayout = view.findViewById(R.id.tabs)
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)

        val adapter = MainAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "연결하기"
                1 -> "주변 목록"
                2 -> "가족"
                else -> null
            }
        }.attach()

//        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                tab.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//                tab.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.sub))
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//                // 필요한 경우 코드 추가
//            }
//        })

    }
}