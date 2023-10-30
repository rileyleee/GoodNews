package com.saveurlife.goodnews.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityMainBinding
import com.saveurlife.goodnews.family.FamilyFragment
import com.saveurlife.goodnews.map.MapFragment
import com.saveurlife.goodnews.mypage.MyPageFragment


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


//        replaceFragment(HomeFragment())
        binding.navigationView.background = null
        binding.navigationView.menu.getItem(2).isEnabled = false
//        binding.navigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.homeFragment -> replaceFragment(HomeFragment())
//                R.id.mapFragment -> replaceFragment(MapFragment())
//                R.id.familyFragment -> replaceFragment(FamilyFragment())
//                R.id.myPageFragment -> replaceFragment(MyPageFragment())
//            }
//            true
//        }


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.mapFragment,
                R.id.familyFragment,
                R.id.myPageFragment,
//                R.id.flashLightFragment
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        binding.navigationView.setupWithNavController(navController)

        //상태 변경
//        binding.myStatusUpdateButtom.setOnClickListener{
//
//        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

    //toolbar 보여주기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    //클릭했을 때 어떤 반응이 일어나게 할건지
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, "알림 기능 구현하기", Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }

}