package com.saveurlife.goodnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.saveurlife.goodnews.databinding.ActivityLoadingBinding
import com.saveurlife.goodnews.tutorial.TutorialActivity

class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed(Runnable {
            // 앱의 MainActivity로 넘어가기
            val i = Intent(this@LoadingActivity, TutorialActivity::class.java)
            startActivity(i)
            // 현재 액티비티 닫기
            finish()
        }, 3000)




//        binding.start.setOnClickListener {
//            val intent = Intent(this, TutorialActivity::class.java)
//            startActivity(intent)
//        }
    }
}