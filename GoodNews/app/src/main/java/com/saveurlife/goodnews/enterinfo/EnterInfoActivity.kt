package com.saveurlife.goodnews.enterinfo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.R

class EnterInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_info)

        val submitButton = findViewById<TextView>(R.id.submit_info)
        val laterButton = findViewById<TextView>(R.id.later_info)

        // 정보 등록 버튼 눌렀을 때, 이벤트
        submitButton.setOnClickListener {
            Toast.makeText(this, "정보 등록 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        // 다음에 등록하기 버튼 눌렀을 때, 이벤트
        laterButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            Toast.makeText(this, "다음에 등록하기 버튼 클릭", Toast.LENGTH_SHORT).show()

        }
    }
}