package com.saveurlife.goodnews.family

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.MapsFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyPlaceAddEditBinding
import com.saveurlife.goodnews.family.FamilyFragment.Mode
import com.saveurlife.goodnews.models.FamilyPlace
import io.realm.kotlin.Realm

class FamilyPlaceAddEditFragment : DialogFragment() {

    private lateinit var binding: FragmentFamilyPlaceAddEditBinding
    private lateinit var geocoder: Geocoder

    private lateinit var mapsFragment: MapsFragment
    private lateinit var realm: Realm

    // 제출 전에 담아둘 변수
    private var tempFamilyPlace: FamilyPlace? = null

    private var mode: Mode? = null
    private var seqNumber: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mode = it.getSerializable("mode") as Mode
            seqNumber = it.getInt("seq")

            when (mode) {
                Mode.READ -> loadDataAndDisplay(seqNumber)
                Mode.EDIT -> loadDataForEdit(seqNumber)
                else -> {} // ADD 모드
            }
        }
    }

    // 데이터 로드 및 표시 (READ 모드)
    private fun loadDataAndDisplay(seq: Int?) {
        seq?.let {
//            val data = loadData(it)
        }
    }

    // 데이터 로드 (EDIT 모드)
    private fun loadDataForEdit(seq: Int?) {
        seq?.let {
//            val data = loadData(it)
            // 데이터를 편집할 수 있는 방식으로 UI 구성
        }
    }

    // Realm에서 데이터 로드
    private fun loadData(seq: Int): FamilyPlace? {
        // Realm 열고 데이터를 받아오기
        return null // 임시
    }

    // 데이터 UI에 표시 (READ 모드)
    private fun displayData(data: FamilyPlace?) {
        data?.let {
            // 데이터 UI에 적용
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyPlaceAddEditBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 구글 서치 박스 ui 변경
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.meetingPlaceAutocompleteFragment) as AutocompleteSupportFragment
        autocompleteFragment.view?.setBackgroundResource(R.drawable.input_stroke_none)
        autocompleteFragment.view?.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
            ?.apply {
                hint = "주소를 검색해 주세요."
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }

        // 등록 버튼 눌렀을 때
        binding.meetingPlaceAddSubmit.setOnClickListener {
            // 닉네임 설정
            val nickname = binding.meetingPlaceNickname.text.toString()
            tempFamilyPlace?.name = nickname

            // 모드에 따라 바뀜
            when (mode) {
                Mode.ADD -> addNewPlace(seqNumber)
                Mode.EDIT -> updatePlace(seqNumber)
                else -> {} // READ 모드
            }
            dismiss() // 다이얼로그 닫기
        }

        binding.meetingPlaceAddCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    // 장소 정보 업데이트 (EDIT 모드)
    private fun updatePlace(seq: Int?) {

    }

    // 새로운 장소를 Realm에 추가하는 메서드
    private fun addNewPlace(seq: Int?) {
        // 서버에 먼저 보내고, placeId 얻어온 다음에 Realm 저장 진행해야됨!!!
        seq?.let { seqNumber ->
            // Realm 인스턴스 열기
            realm = Realm.open(GoodNewsApplication.realmConfiguration)

            realm.writeBlocking {
                // 새로운 FamilyPlace 객체 생성 및 속성 설정
                copyToRealm(FamilyPlace().apply {
                    this.seq = seqNumber
                    this.name = tempFamilyPlace?.name ?: ""
                    this.address = tempFamilyPlace?.address ?: ""
                    this.latitude = tempFamilyPlace?.latitude ?: 0.0
                    this.longitude = tempFamilyPlace?.longitude ?: 0.0
                })
            }

            realm.close()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapsFragment = MapsFragment()
        childFragmentManager.beginTransaction().apply {
            add(R.id.meetingPlaceMapView, mapsFragment)
            commit()
        }

        geocoder = Geocoder(requireActivity())

        // 모드에 따라 meetingPlaceAddSubmit의 텍스트 변경
        when (mode) {
            Mode.ADD -> binding.meetingPlaceAddSubmit.text = "장소 등록"
            Mode.EDIT -> binding.meetingPlaceAddSubmit.text = "장소 수정"
            else -> binding.meetingPlaceAddSubmit.text = "수정하기"
        }

        // AutocompleteSupportFragment 설정
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.meetingPlaceAutocompleteFragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // 사용자가 선택한 장소로 지도 이동
                place.latLng?.let {
                    mapsFragment.setLocation(it.latitude, it.longitude)

                    // FamilyPlace에 저장
                    tempFamilyPlace = FamilyPlace().apply {
                        this.address = place.address ?: ""
                        this.latitude = it.latitude
                        this.longitude = it.longitude
                    }
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.i("AutocompleteError", "An error occurred: $status")
            }
        })
    }

}