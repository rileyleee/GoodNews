package com.saveurlife.goodnews.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityBottomNaviBinding

class BottomNaviActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBottomNaviBinding
    private val TAG_HOME = "home_fragment"
    private val TAG_MAP = "map_fragment"
    private val TAG_FAMILY = "family_fragment"
    private val TAG_MY_PAGE = "my_page_fragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.mapFragment -> setFragment(TAG_MAP, MapFragment())
                R.id.familyFragment -> setFragment(TAG_MAP, FamilyFragment())
                R.id.myPageFragment-> setFragment(TAG_MY_PAGE, MyPageFragment())
            }
            true
        }
    }
    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val map = manager.findFragmentByTag(TAG_MAP)
        val family = manager.findFragmentByTag(TAG_FAMILY)
        val myPage = manager.findFragmentByTag(TAG_MY_PAGE)

        if (home != null){
            fragTransaction.hide(home)
        }
        if (map != null){
            fragTransaction.hide(map)
        }
        if (family != null){
            fragTransaction.hide(family)
        }
        if (myPage != null) {
            fragTransaction.hide(myPage)
        }

        if (tag == TAG_HOME) {
            if (home!=null){
                fragTransaction.show(home)
            }
        }
        else if (tag == TAG_MAP) {
            if (map != null) {
                fragTransaction.show(map)
            }
        }
        else if (tag == TAG_FAMILY){
            if (family != null){
                fragTransaction.show(family)
            }
        }
        else if (tag == TAG_MY_PAGE){
            if (myPage != null){
                fragTransaction.show(myPage)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}