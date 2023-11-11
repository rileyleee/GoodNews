package com.saveurlife.goodnews.map

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentEmergencyInfoDialogBinding


class EmergencyInfoDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentEmergencyInfoDialogBinding

    // 위치 정보 초기화
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmergencyInfoDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        latitude = arguments?.getDouble("latitude", 0.0) ?: 0.0
        longitude = arguments?.getDouble("longitude", 0.0) ?: 0.0

        Log.v("정보 공유 창에 보낼 현재 위도", "$latitude")
        Log.v("정보 공유 창에 보낼 현재 경도", "$longitude")

        // 토글 상태 변경 시 색상 업데이트
        binding.emergencyStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { // 스위치가 안전 상태
                binding.dangerTextView.visibility = View.GONE
                binding.safeTextView.visibility = View.VISIBLE
            } else {
                binding.dangerTextView.visibility = View.VISIBLE
                binding.safeTextView.visibility = View.GONE
            }
        }

        // 취소 버튼 클릭했을 때
        binding.emergencyAddCancel.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }

        return binding.root
    }


}