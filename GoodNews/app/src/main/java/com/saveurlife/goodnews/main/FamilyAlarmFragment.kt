package com.saveurlife.goodnews.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyAlarmBinding
import com.saveurlife.goodnews.enterinfo.EnterInfoActivity

class FamilyAlarmFragment : DialogFragment() {
    private lateinit var binding: FragmentFamilyAlarmBinding
    val sharedPreferences = GoodNewsApplication.preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyAlarmBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //가족 등록 버튼 클릭시 가족 등록 페이지로 이동
        binding.familyRegist.setOnClickListener {
            // 현재 Fragment의 NavController를 얻기
            val navController = findNavController()
            // framilyFragment로 이동
            navController.navigate(R.id.familyFragment)
            // 다이얼로그 닫기
            dismiss()
        }

        // 다음에 하기 클릭시 다이얼로그 닫기
        binding.familyNext.setOnClickListener {
            val preferencesUtil = PreferencesUtil(requireContext())
            preferencesUtil.setBoolean("familyAlarmIgnore", true)
            dismiss()
        }
        return binding.root
    }
}