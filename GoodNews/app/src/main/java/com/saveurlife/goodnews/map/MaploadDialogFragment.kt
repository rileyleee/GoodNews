package com.saveurlife.goodnews.map

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.databinding.FragmentMaploadDialogBinding

class MaploadDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentMaploadDialogBinding
    private val sharedPreferences = GoodNewsApplication.preferences

    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "canLoadMapFragment" && sharedPreferences.getBoolean(key, false)) {
                dismiss()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMaploadDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 맵 로딩 중 닫기 버튼 눌렀을 때
        binding.maploadClose.setOnClickListener {
            dismiss()
        }

        // 주기적으로 SharedPreferences의 canLoadMapFragment 값 변경 감지하는 리스너
        sharedPreferences.preferences.registerOnSharedPreferenceChangeListener(listener)

        return binding.root
    }

    override fun onDestroyView() { // 해당 프래그먼트 닫힐 때 리스너 해제
        super.onDestroyView()
        sharedPreferences.preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }


}