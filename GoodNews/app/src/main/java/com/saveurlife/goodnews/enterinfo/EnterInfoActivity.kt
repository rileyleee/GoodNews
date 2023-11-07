package com.saveurlife.goodnews.enterinfo

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.databinding.ActivityEnterInfoBinding
import com.saveurlife.goodnews.main.PermissionsUtil
import java.util.Calendar

class EnterInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterInfoBinding
    private lateinit var permissionsUtil: PermissionsUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEnterInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 위험 권한 요청
        permissionsUtil = PermissionsUtil(this)
        permissionsUtil.requestAllPermissions()

        initDateSpinners() // Spinner 데이터 설정 함수 호출
        initBloodSpinners()

        // 정보 등록 버튼 눌렀을 때, 이벤트
        binding.submitInfo.setOnClickListener {
            Toast.makeText(this, "정보 등록 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        // 다음에 등록하기 버튼 눌렀을 때, 이벤트
        binding.laterInfo.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            Toast.makeText(this, "다음에 등록하기 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initDateSpinners() {
        // 년도 데이터 설정
        val years = ArrayList<String>()
        for (i in Calendar.getInstance().get(Calendar.YEAR) - 100..Calendar.getInstance()
            .get(Calendar.YEAR)) {
            years.add(i.toString())
        }
        val yearAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, years).also {
            it.insert("선택", 0)
        }
        binding.yearSpinner.adapter = yearAdapter
        binding.yearSpinner.setSelection(0)

        // 월 데이터 설정
        val months = ArrayList<String>()
        for (i in 1..12) {
            months.add(i.toString())
        }
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months).also {
            it.insert("선택", 0)
        }
        binding.monthSpinner.adapter = monthAdapter
        binding.monthSpinner.setSelection(0)

        // 일 데이터 설정 (일단 1~31로 설정)
        val days = ArrayList<String>()
        for (i in 1..31) {
            days.add(i.toString())
        }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days).also {
            it.insert("선택", 0)
        }
        binding.daySpinner.adapter = dayAdapter
        binding.daySpinner.setSelection(0)
    }

    private fun initBloodSpinners() {
        // rh인자
        val rhTypes = listOf("선택", "RH+", "RH-", "모름")
        val rhAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rhTypes).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.rhSpinner.adapter = rhAdapter
        binding.rhSpinner.setSelection(0)

        // 혈액형
        val bloodTypes = listOf("선택", "A", "B", "AB", "O")
        val bloodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodTypes).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.bloodSpinner.adapter = bloodAdapter
        binding.bloodSpinner.setSelection(0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        permissionsUtil.dismissDialog()
        super.onDestroy()
    }


}