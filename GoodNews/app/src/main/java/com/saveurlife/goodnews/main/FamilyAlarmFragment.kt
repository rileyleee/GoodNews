package com.saveurlife.goodnews.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyAlarmBinding
import com.saveurlife.goodnews.enterinfo.EnterInfoActivity
import com.saveurlife.goodnews.family.FamilyActivity

class FamilyAlarmFragment : DialogFragment() {
    private lateinit var binding: FragmentFamilyAlarmBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyAlarmBinding.inflate(inflater, container, false)

        //가족 등록 버튼 클릭시 가족 등록 페이지로 이동
        binding.familyRegist.setOnClickListener {
            val intent = Intent(requireContext(), FamilyActivity::class.java) //이동
            startActivity(intent)
            dismiss() // 다이얼로그 닫기
        }

        // 다음에 하기 클릭시 다이얼로그 닫기
        binding.familyNext.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
}