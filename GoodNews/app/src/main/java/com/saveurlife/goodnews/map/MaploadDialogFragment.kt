package com.saveurlife.goodnews.map

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.databinding.FragmentMaploadDialogBinding

class MaploadDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentMaploadDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMaploadDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 맵 로딩 중 닫기 버튼 눌렀을 때
        binding.maploadClose.setOnClickListener {
            dismiss()
        }

        // 주기적으로 SharedPreferences 값 확인해서 true 이면 mapfragment로 자동 이동


        return binding.root
    }



}