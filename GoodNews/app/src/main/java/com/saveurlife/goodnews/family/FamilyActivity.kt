package com.saveurlife.goodnews.family

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityFamilyBinding

class FamilyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFamilyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_family)
    }
}