package com.saveurlife.goodnews.tutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.enterinfo.EnterInfoActivity

class TutorialActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        viewPager = findViewById(R.id.tutorial_view_pager)

        initViews()
    }

    //추가 정보 입력 페이지로 이동
    fun navigateToOtherActivity() {
        val intent = Intent(this, EnterInfoActivity::class.java)
        startActivity(intent)
    }

    private fun initViews() {
        //튜토리얼 설명 list 생성
        val tutorialDataList = listOf(
            TutorialData(
                "WIFI DIRECT로 채팅",
                "통신 불가능이어도 \n 100m 이내 사람이 있다면 \n 채팅 가능 어쩌구",
                R.drawable.tutorial_1
            ),
            TutorialData(
                "지도 내 주변 대피소 현황",
                "위급 시 대피할 수 있는 대피소, \n 병원, 식료품점을 지도에 한눈에 \n 볼 수 있습니다.",
                R.drawable.tutorial_2
            ),
            TutorialData(
                "소중한 나의 가족 위치",
                "미리 등록해둔 가족의 위치를\n 지도를 통해 확인할 수 있고\n 어쩌구",
                R.drawable.tutorial_3
            ),
            TutorialData(
                "나만의 모스 부호",
                "나의 위급함을 전달하기 위해 \n 나만의 모스 부호를 만들어\n 보여줄 수 있고 해석 어쩌구",
                R.drawable.tutorial_4
            )
        )


        //viewPager에 어뎁터 설정
        val isLastPage = true
        val adapter = TutorialPageAdapter(tutorialDataList, isLastPage)
        //viewPager의 어뎁터를 adapter로 설정
        viewPager.adapter = adapter

        val compositePageTransformer = CompositePageTransformer()
//        compositePageTransformer.addTransformer(ScaleInTransformer())
//        compositePageTransformer.addTransformer(MarginPageTransformer(40))

        //viewPager의 방향 수평으로 설정
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.setPageTransformer(compositePageTransformer)
    }
}