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
import com.saveurlife.goodnews.databinding.FragmentEmergencyListDialogBinding
import com.saveurlife.goodnews.databinding.FragmentMaploadDialogBinding
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.models.MapInstantInfo

class EmergencyListDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentEmergencyListDialogBinding
    private var closeInfoList: MutableList<MapInstantInfo> = mutableListOf()

    companion object {
        private const val CLOSE_INFO_KEY = "close_info"

        fun newInstance(closeInfo: MutableList<MapInstantInfo>): EmergencyListDialogFragment {
            val fragment = EmergencyListDialogFragment()
            val args = Bundle().apply {
//                putParcelableArrayList(CLOSE_INFO_KEY, ArrayList(closeInfo))
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmergencyListDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 닫기 버튼 눌렀을 때
        binding.emergencyListClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}