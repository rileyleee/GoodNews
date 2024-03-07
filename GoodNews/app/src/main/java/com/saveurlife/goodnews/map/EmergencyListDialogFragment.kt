package com.saveurlife.goodnews.map

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentEmergencyListDialogBinding
import com.saveurlife.goodnews.databinding.FragmentMaploadDialogBinding
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.models.MapInstantInfo

class EmergencyListDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentEmergencyListDialogBinding
    private lateinit var closeInfo: List<MapInstantInfo>

    companion object {
        private const val ARG_CLOSE_INFO = "closeInfo"
        private const val ARG_FACILITY_NAME = "facilityName"
        fun newInstance(closeInfo: List<MapInstantInfo>, facilityName: String): EmergencyListDialogFragment {
            val fragment = EmergencyListDialogFragment()
            val args = Bundle().apply {
                putSerializable(ARG_CLOSE_INFO, ArrayList(closeInfo))
                putString(ARG_FACILITY_NAME, facilityName)
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

        // 받아온 closeInfo 리스트를 가져옵니다.
        closeInfo = arguments?.getSerializable("closeInfo") as List<MapInstantInfo>
        // closeInfo를 역순으로 정렬
        closeInfo = closeInfo.reversed()

        // 받아온 facilityName을 가져옵니다.
        val facilityName = arguments?.getString(ARG_FACILITY_NAME)
        binding.emergencyFacilityNameWrap.text = facilityName
        // RecyclerView 설정
        val recyclerView = binding.emergencyListWrap
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = EmergencyListAdapter(closeInfo)


        // 닫기 버튼 눌렀을 때
        binding.emergencyListClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}