package com.saveurlife.goodnews.family

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyBinding
import com.saveurlife.goodnews.flashlight.FlashType
import com.saveurlife.goodnews.flashlight.FlashlightData
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.service.DeviceStateService

class FamilyFragment : Fragment() {
    enum class Mode { ADD, READ, EDIT }

    private lateinit var familyListRecyclerView: RecyclerView
    private lateinit var binding: FragmentFamilyBinding
    private lateinit var deviceStateService: DeviceStateService
    companion object{
        lateinit var familyEditText:TextView
        lateinit var familyListAdapter: FamilyListAdapter
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyBinding.inflate(inflater, container, false)
        deviceStateService = DeviceStateService()
        familyListAdapter = FamilyListAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        familyEditText = view.findViewById(R.id.familyEditText)
        // 첫 번째 모임 장소 layout 클릭 했을 때
        binding.meetingPlaceFirst.setOnClickListener {
            val placeId = 1 // 장소 id
            showMeetingDialog(placeId)
        }

        // 가족 신청 버튼을 클릭 했을 때
        binding.familyAddButton.setOnClickListener {
            showAddDialog()
        }

        familyListRecyclerView = view.findViewById(R.id.familyList)
        familyListRecyclerView.layoutManager = LinearLayoutManager(context)
        familyListAdapter = FamilyListAdapter()
        familyListRecyclerView.adapter = familyListAdapter

        familyListAdapter.addFamilyWait("test", "test", "가능가능")
        familyListAdapter.addFamilyInfo("testInfo", Status.HEALTHY, "2023-11-15 10:11:12")

    }

    // 모달 창 띄워주는 것
    private fun showMeetingDialog(placeId: Int? = null) {
        val dialogFragment = FamilyPlaceAddEditFragment().apply {
//            arguments = Bundle().apply {
//                //데이터가 있으면 읽기 모드, 없으면 추가 모드
//                putSerializable("mode", if (placeId == null) Mode.ADD else Mode.READ)
//                putInt("placeId", placeId ?: -1)
//            }
        }
        dialogFragment.show(childFragmentManager, "FamilyPlaceAddEditFragment")
    }

    private fun showAddDialog() {
        val dialogFragment = FamilyAddFragment()
        if(deviceStateService.isNetworkAvailable(requireContext())){
            dialogFragment.show(childFragmentManager, "FamilyAddFragment")
        }else{
            Toast.makeText(requireContext(), "네트워크 상태가 불안정합니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
        }
    }
}