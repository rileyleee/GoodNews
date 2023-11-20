package com.saveurlife.goodnews.family

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.api.MemberAPI
import com.saveurlife.goodnews.api.WaitInfo
import com.saveurlife.goodnews.databinding.FragmentFamilyBinding
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.service.DeviceStateService
import com.saveurlife.goodnews.models.FamilyPlace
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import com.saveurlife.goodnews.service.UserDeviceInfoService

class FamilyFragment : Fragment() {
    enum class Mode { ADD, READ, EDIT }

    private lateinit var familyListRecyclerView: RecyclerView
    private lateinit var binding: FragmentFamilyBinding
    private lateinit var realm: Realm

    // 클래스 레벨 변수로 장소 데이터 저장
    private var familyPlaces: List<FamilyPlace> = listOf()

    private lateinit var deviceStateService: DeviceStateService
    private lateinit var userDeviceInfoService: UserDeviceInfoService
    companion object{
        lateinit var familyEditText:TextView
        lateinit var familyListAdapter: FamilyListAdapter
        lateinit var numToStatus:Map<Int, Status>
        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        lateinit var familyAPI: FamilyAPI
        lateinit var memberAPI: MemberAPI
        lateinit var memberId:String
//        lateinit var context1: Context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyBinding.inflate(inflater, container, false)
        deviceStateService = DeviceStateService()
        familyListAdapter = FamilyListAdapter(requireContext())
        userDeviceInfoService = UserDeviceInfoService(requireContext())
        memberId = userDeviceInfoService.deviceId
        familyEditText = binding.familyEditText
        familyAPI = FamilyAPI()
        memberAPI = MemberAPI()
//        context1 = requireContext()
        numToStatus = mapOf(
            0 to Status.HEALTHY,
            1 to Status.INJURED,
            2 to Status.DECEASED,
            3 to Status.NOT_SHOWN
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 로딩, UI 업데이트
        loadFamilyPlaces()

        // 각 장소 클릭 이벤트 처리
        binding.meetingPlaceFirst.setOnClickListener { handlePlaceClick(seq = 1) }
        binding.meetingPlaceSecond.setOnClickListener { handlePlaceClick(seq = 2) }
        binding.meetingPlaceThird.setOnClickListener { handlePlaceClick(seq = 3) }

        // 가족 신청 버튼을 클릭 했을 때
        binding.familyAddButton.setOnClickListener {
            showAddDialog()
        }

        familyListRecyclerView = view.findViewById(R.id.familyList)
        familyListRecyclerView.layoutManager = LinearLayoutManager(context)
        familyListAdapter = FamilyListAdapter(requireContext())
        familyListRecyclerView.adapter = familyListAdapter

        familyListAdapter.addList()

    }

    // Realm에서 데이터 로드 및 UI 업데이트
    private fun loadFamilyPlaces() {
        // Realm 인스턴스 열기
        realm = Realm.open(GoodNewsApplication.realmConfiguration)

        try {
            // Realm 쿼리 실행 및 결과 저장
            val places: RealmResults<FamilyPlace> = realm.query<FamilyPlace>().find()
            familyPlaces = realm.copyFromRealm(places)

            // UI 업데이트
            updatedUIWithFamilyPlaces(familyPlaces)
        } finally {
            // Realm 인스턴스 닫기
            realm.close()
        }
    }

    private fun updatedUIWithFamilyPlaces(familyPlaces: List<FamilyPlace>) {
        familyPlaces.forEach { place ->
            val statusColor = if (place.canUse == true) ContextCompat.getColor(
                requireContext(),
                R.color.safe
            ) else ContextCompat.getColor(requireContext(), R.color.danger)

            val statusText = if (place.canUse == true) "안전" else "위험"

            when (place.seq) {
                1 -> {
                    binding.meetingNameFirst.text = place.name
                    binding.meetingStatusFirst.text = statusText
                    binding.meetingPlaceFirst.backgroundTintList = null
                    binding.meetingStatusFirst.setTextColor(statusColor)
                }

                2 -> {
                    binding.meetingNameSecond.text = place.name
                    binding.meetingStatusSecond.text = statusText
                    binding.meetingPlaceSecond.backgroundTintList = null
                    binding.meetingStatusSecond.setTextColor(statusColor)
                }

                3 -> {
                    binding.meetingNameThird.text = place.name
                    binding.meetingStatusThird.text = statusText
                    binding.meetingPlaceThird.backgroundTintList = null
                    binding.meetingStatusThird.setTextColor(statusColor)
                }
            }
        }
    }

    // 장소 클릭 했을 때
    private fun handlePlaceClick(seq: Int) {
        val place = familyPlaces.find { it.seq == seq }
        val mode = if (place == null) Mode.ADD else Mode.READ

        showMeetingDialog(seq, mode)
    }

    // 모달 창 띄워주는 것
    private fun showMeetingDialog(seq: Int, mode: Mode) {
        val dialogFragment = FamilyPlaceAddEditFragment().apply {
            arguments = Bundle().apply {
                //데이터가 있으면 읽기 모드, 없으면 추가 모드
                putSerializable("mode", mode)
                putInt("seq", seq)
            }
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