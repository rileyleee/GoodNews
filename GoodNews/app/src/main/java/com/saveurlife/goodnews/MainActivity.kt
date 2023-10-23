package com.saveurlife.goodnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.saveurlife.goodnews.tutorial.TutorialActivity

class MainActivity : AppCompatActivity() {
    private lateinit var startButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //버튼 찾기
        startButton = findViewById(R.id.start)

        //클릭 시 페이지 이동
        startButton.setOnClickListener {
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
        }
    }
}