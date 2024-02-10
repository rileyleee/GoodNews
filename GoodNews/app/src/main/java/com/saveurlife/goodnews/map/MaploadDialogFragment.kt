package com.saveurlife.goodnews.map

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
import com.saveurlife.goodnews.databinding.FragmentMaploadDialogBinding
import com.saveurlife.goodnews.main.PreferencesUtil

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

        return binding.root
    }

}