package com.saveurlife.goodnews.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.alarm.AlarmActivity
import com.saveurlife.goodnews.alarm.AlarmFragment
import com.saveurlife.goodnews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //가족 등록 모달창
        val dialog = FamilyAlarmFragment()
        dialog.show(supportFragmentManager, "FamilyAlarmFragment")


        //상단바 모달창
        setSupportActionBar(binding.toolbar)

        //Fragment 갈아 끼우기
        binding.navigationView.background = null
        binding.navigationView.menu.getItem(2).isEnabled = false

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.mapFragment,
                R.id.familyFragment,
                R.id.myPageFragment
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        binding.navigationView.setupWithNavController(navController)

        //상태 변경
//        binding.myStatusUpdateButtom.setOnClickListener{
//
//        }

        //알림창 갔다가 다시 돌아올 때 toolbar, navigationBottom 원래대로 돌리기
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                // 프래그먼트 스택에 프래그먼트가 없을 때 Toolbar와 BottomNavigationView 표시
                binding.toolbar.visibility = View.VISIBLE
                binding.navigationView.visibility = View.VISIBLE
                binding.bottomAppBar.visibility = View.VISIBLE
                binding.mainCircleAddButton.visibility = View.VISIBLE
            }
        }
    }

    //toolbar 보여주기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, AlarmActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }



    //알람창
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_search -> {
//                // AlarmFragment 인스턴스 생성
//                val alarmFragment = AlarmFragment()
//
//                // Toolbar와 BottomNavigationView 숨기기
//                binding.toolbar.visibility = View.GONE
//                binding.navigationView.visibility = View.GONE
//                binding.bottomAppBar.visibility = View.GONE
//                binding.mainCircleAddButton.visibility = View.GONE
//
//                // FragmentTransaction을 사용하여 Fragment 교체
//                val transaction = supportFragmentManager.beginTransaction()
//                transaction.setCustomAnimations(
//                    R.anim.slide_in_right, R.anim.slide_out_left
//                )
//                transaction.replace(R.id.nav_host_fragment, alarmFragment)
//                transaction.addToBackStack(null) // Back 버튼을 눌렀을 때 이전 Fragment로 돌아갈 수 있도록 스택에 추가
//                transaction.commit()
//                return true
//            }
//            else -> {
//                return super.onOptionsItemSelected(item)
//            }
//        }
//    }

}