package com.saveurlife.goodnews.family

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyAddBinding

class FamilyAddFragment : DialogFragment() {
    private lateinit var binding: FragmentFamilyAddBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyAddBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 신청 버튼 클릭했을 때
        binding.familyAddSubmit.setOnClickListener {
            // 여기에 추가되어야 하는 것 : 신청을 눌렀을 때, 해당 신청 요청 box도 하나 띄워줘야 한다.
            Toast.makeText(activity, "가족 신청 버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show()
            dismiss() // 다이얼로그 닫기
        }

        // 취소 버튼 클릭했을 때
        binding.familyAddCancel.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }

        return binding.root
    }

}