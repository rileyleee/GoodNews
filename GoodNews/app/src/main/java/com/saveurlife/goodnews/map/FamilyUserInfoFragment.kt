package com.saveurlife.goodnews.map

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyUserInfoBinding

class FamilyUserInfoFragment : DialogFragment() {
    private lateinit var binding: FragmentFamilyUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyUserInfoBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // 데이터 받아와 화면에 표시
        arguments?.let { bundle ->  }

        // 닫기 버튼 클릭했을 때
        binding.familyCloseButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}