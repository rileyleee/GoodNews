package com.saveurlife.goodnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saveurlife.goodnews.databinding.ActivityLoadingBinding
import com.saveurlife.goodnews.tutorial.TutorialActivity

class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.start.setOnClickListener {
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
        }
    }
}