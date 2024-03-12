package com.saveurlife.goodnews.tmap

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.databinding.FragmentTMapRouteGuideDialogBinding

class TMapRouteGuideDialogFragment() : DialogFragment() {
    private lateinit var binding: FragmentTMapRouteGuideDialogBinding

    companion object {
        fun newInstance(selectedFacilityName: String): TMapRouteGuideDialogFragment {
            val fragment = TMapRouteGuideDialogFragment()
            val args = Bundle()
            args.putString("selectedFacilityName", selectedFacilityName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTMapRouteGuideDialogBinding.inflate(inflater, container, false)

        val selectedFacilityName = arguments?.getString("selectedFacilityName")
        binding.tMapDialogTextView.text = "${selectedFacilityName}(으)로 \n가는 길 안내를 시작합니다."

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 3초 후에 다이얼로그를 닫기 위한 Handler
        val handler = Handler(Looper.getMainLooper())

        // 3000 밀리초 (3초) 후에 다이얼로그를 닫기
        val delayMillis = 3000
        handler.postDelayed({
            dismiss()
        }, delayMillis.toLong())

        return binding.root
    }
}