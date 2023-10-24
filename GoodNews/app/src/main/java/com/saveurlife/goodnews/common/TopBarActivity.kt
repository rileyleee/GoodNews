package com.saveurlife.goodnews.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityTopBarBinding

class TopBarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTopBarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopBarBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}