package com.saveurlife.goodnews.family

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.Display.Mode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.MapsFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyPlaceAddEditBinding
import java.io.IOException

class FamilyPlaceAddEditFragment : DialogFragment() {

    private lateinit var binding: FragmentFamilyPlaceAddEditBinding
    private lateinit var geocoder: Geocoder

    private lateinit var mapsFragment: MapsFragment

    //위도 경도 초기화 임시@@
    var latitude: Double = -34.0
    var longitude: Double = 151.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val mode = it.getSerializable("mode") as Mode
            val placeId = it.getInt("placeId")

//            updateUIForMode(mode, placeId)
        }
    }

//    private fun updateUIForMode(mode: Mode, dataId: Int) {
//        when (mode) {
//            Mode.ADD -> { /* 추가 모드 설정 */
//
//            }
//
//            Mode.READ -> {
//                // 데이터 로드 및 UI 업데이트
//                val data = loadData(dataId)
//                displayData(data)
//            }
//
//            Mode.EDIT -> { /* 수정 모드 설정 */
//            }
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyPlaceAddEditBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 등록 버튼 눌렀을 때
        binding.meetingPlaceAddSubmit.setOnClickListener {
            // 추가 @@

            dismiss() // 다이얼로그 닫기
        }

        binding.meetingPlaceAddCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapsFragment = MapsFragment()
        childFragmentManager.beginTransaction().apply {
            add(R.id.meetingPlaceMapView, mapsFragment)
            commit()
        }

        geocoder = Geocoder(requireActivity())

        binding.meetingPlaceSearch.setOnClickListener {
            val address = binding.meetingPlaceEditText.text.toString()
            searchAddress(address)
        }
    }

    private fun searchAddress(address: String) {
        if (address.isEmpty()) {
            Toast.makeText(activity, "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
//            val geocodeListener = Geocoder.GeocodeListener { addresses ->
//                Log.i("resultText", addresses.toString())
//            }
//
            val addressList = geocoder.getFromLocationName(address, 10)
            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    val location = addressList[0]
//                    binding.familyPlaceResultList.text = location.toString()
                    Log.i("resultText", addressList.toString())
                    mapsFragment.setLocation(location.latitude, location.longitude)  // 지도 업데이트
                } else {
                    Toast.makeText(activity, "해당 주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "주소 변환 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

//        try {
//            val addressList = geocoder.getFromLocationName(address, 1)
//            if (addressList.isNotEmpty()) {
//                val location = addressList[0]
//                binding.latitudeEditText.setText(location.latitude.toString())
//                binding.longitudeEditText.setText(location.longitude.toString())
//            } else {
//                Toast.makeText(activity, "해당 주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(activity, "주소 변환 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//        }
}