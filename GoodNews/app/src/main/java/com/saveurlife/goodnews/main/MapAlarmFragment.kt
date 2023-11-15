package com.saveurlife.goodnews.main

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
import com.saveurlife.goodnews.databinding.FragmentMapAlarmBinding

class MapAlarmFragment : DialogFragment() {
    private lateinit var binding: FragmentMapAlarmBinding
    val sharedPreferences = GoodNewsApplication.preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapAlarmBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다운로드 받기 버튼 눌렀을 때
        binding.mapDownloadMove.setOnClickListener {
            // sharedPreference에 저장
            val preferencesUtil = PreferencesUtil(requireContext())
            preferencesUtil.setBoolean("mapDownloadIgnore", true)

            val navController = findNavController()
            navController.navigate(R.id.myPageFragment)
            dismiss()
        }

        // 다시 보지 않기 눌렀을 때
        binding.PassMapDownload.setOnClickListener {
            // sharedPreference에 저장
            val preferencesUtil = PreferencesUtil(requireContext())
            preferencesUtil.setBoolean("mapDownloadIgnore", true)
            dismiss()
        }

        return binding.root
    }

}