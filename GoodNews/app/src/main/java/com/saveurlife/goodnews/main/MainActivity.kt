package com.saveurlife.goodnews.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.common.FamilyFragment
import com.saveurlife.goodnews.common.HomeFragment
import com.saveurlife.goodnews.common.MapFragment
import com.saveurlife.goodnews.common.MyPageFragment
import com.saveurlife.goodnews.databinding.ActivityMainBinding
import com.saveurlife.goodnews.tutorial.TutorialActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG_HOME = "home_fragment"
    private val TAG_MAP = "map_fragment"
    private val TAG_FAMILY = "family_fragment"
    private val TAG_MY_PAGE = "my_page_fragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //가족 등록 모달창
        val dialog = FamilyAlarmFragment()
        dialog.show(supportFragmentManager, "FamilyAlarmFragment")

        //상단바 모달창
        setSupportActionBar(binding.toolbar)

        //상태 변경
        binding.myStatusUpdateButtom.setOnClickListener{

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

}