package com.saveurlife.goodnews.tutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.authority.AuthorityActivity
import com.saveurlife.goodnews.databinding.ActivityMainBinding
import com.saveurlife.goodnews.databinding.ActivityTutorialBinding
import com.saveurlife.goodnews.enterinfo.EnterInfoActivity

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        TabLayoutMediator(
            binding.tabLayout,
            binding.tutorialViewPager,
        ){
            tab, position ->
            binding.tutorialViewPager.currentItem = tab.position
        }.attach()
    }

    private fun initViews() {
        //튜토리얼 설명 list 생성
        val tutorialDataList = listOf(
            TutorialData(
                "BLE로 오프라인 채팅",
                "통신이 불가능해도 Mesh Network로\n연결된 사람들의 정보를 볼 수 있어요!",
                R.drawable.tutorial_1
            ),
            TutorialData(
                "주변 대피소 현황 지도",
                "위급 시, 대피할 수 있는 대피소 / 병원 /\n식료품점을 한눈에 볼 수 있어요!",
                R.drawable.tutorial_2
            ),
            TutorialData(
                "소중한 나의 가족 위치",
                "미리 등록해둔 가족의 위치를 지도를 통해\n확인하고, 약속장소를 등록할 수 있어요!",
                R.drawable.tutorial_3
            ),
            TutorialData(
                "나만의 모스 부호",
                "대화가 불가능할 때, 나만의 모스 부호를\n만들어 상대방과 소통해 보세요!",
                R.drawable.tutorial_4
            )
        )


        //viewPager에 어뎁터 설정
        val isLastPage = true
        val adapter = TutorialPageAdapter(tutorialDataList, isLastPage)
        //viewPager의 어뎁터를 adapter로 설정
        binding.tutorialViewPager.adapter = adapter

        val compositePageTransformer = CompositePageTransformer()
//        compositePageTransformer.addTransformer(ScaleInTransformer())
//        compositePageTransformer.addTransformer(MarginPageTransformer(40))

        //viewPager의 방향 수평으로 설정
        binding.tutorialViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.tutorialViewPager.setPageTransformer(compositePageTransformer)
    }

    //추가 정보 입력 페이지로 이동
    fun navigateToOtherActivity() {
        val intent = Intent(this, AuthorityActivity::class.java)
        startActivity(intent)
    }
}