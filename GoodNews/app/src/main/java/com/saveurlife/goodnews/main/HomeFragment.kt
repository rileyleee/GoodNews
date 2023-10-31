package com.saveurlife.goodnews.main

import MainAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout


        val dogFragment = MainAroundListFragment()
        val catFragment = MainFamilyAroundListFragment()

        val fragments = arrayListOf<Fragment>(dogFragment, catFragment)
        val tabAdapter = object : FragmentStateAdapter(this) {

            override fun getItemCount(): Int {
                return fragments.size
            }
            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

//        binding.viewPager2.adapter = tabAdapter
//        TabLayoutMediator(binding.tabLayout, binding.viewPager2) {tab,position ->
//            when (position) {
//                0 -> tab.setText(R.string.tabDogText)
//                else -> tab.setText(R.string.tabCatText)
//            }
//        }.attach()

//        tabLayout.addTab(tabLayout.newTab().setText("Fragment1"))
//        tabLayout.addTab(tabLayout.newTab().setText("Fragment2"))
//        tabLayout.addTab(tabLayout.newTab().setText("Fragment3"))
//
//
//        val viewPager: ViewPager = binding.viewPager
//
//
//        val adapter = MainAdapter(supportFragmentManager, tabLayout.tabCount)
//
//        viewPager.adapter = adapter
//
//        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
//
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                viewPager.currentItem = tab.position
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//            }
//        })


        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}