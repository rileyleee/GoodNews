package com.saveurlife.goodnews.tmap

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentTMapDialogBinding
import com.saveurlife.goodnews.main.PreferencesUtil

class TMapDialogFragment : DialogFragment(){
    private lateinit var binding: FragmentTMapDialogBinding
    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTMapDialogBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //확인 버튼 클릭 시 티맵 지도 fragment로
        binding.tMapOkButton.setOnClickListener {
            // 다이얼로그 닫기
            dismiss()
        }

        //다시 보지 않기 버튼 클릭 시 tMapDialogIgnore을 true 로 변경 후 티맵 지도 fragment로 이동
        binding.dontSeeAgainButton.setOnClickListener {
            val preferencesUtil = PreferencesUtil(requireContext())
            preferencesUtil.setBoolean("tMapDialogIgnore", true)
            dismiss()
        }

        return binding.root
    }
}