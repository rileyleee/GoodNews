package com.saveurlife.goodnews.family

import android.content.Context
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
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.api.PlaceDetailInfo
import com.saveurlife.goodnews.databinding.FragmentFamilyPlaceAddEditBinding
import com.saveurlife.goodnews.family.FamilyFragment.Mode
import com.saveurlife.goodnews.models.FamilyPlace
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.service.DeviceStateService
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

// 가족 신청 추가
class FamilyPlaceAddEditFragment(private val familyFragment: FamilyFragment, private val context: Context) : DialogFragment() {

    private lateinit var binding: FragmentFamilyPlaceAddEditBinding
    private lateinit var geocoder: Geocoder

    private lateinit var mapsFragment: MapsFragment
    private var familyAPI = FamilyAPI()

    // 제출 전에 담아둘 변수
    private var tempFamilyPlace: FamilyPlace? = null

    private var mode: Mode? = null
    private var seqNumber: Int = 0

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
    private fun loadDataAndDisplay(seq: Int) {
        seq.let {
            val data = loadData(seq)
            displayData(data)
        }
    }

    // 데이터 로드 (EDIT 모드)
    private fun loadDataForEdit(seq: Int?) {
        seq?.let {
            val data = loadData(it)
            // 데이터를 편집할 수 있는 방식으로 UI 구성
        }
    }

    // Realm에서 데이터 로드 (seq에 맞는 데이터)
    private fun loadData(seq: Int): FamilyPlace? {
        // Realm 열고 데이터를 받아오기
        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val data: FamilyPlace? = realm.query<FamilyPlace>("seq == $0", seq).first().find()

        // Realm 객체를 일반 데이터 클래스로 변환 (복사)
        val copiedData: FamilyPlace? = data?.let {
            FamilyPlace(
                placeId = it.placeId,
                name = it.name,
                address = it.address,
                latitude = it.latitude,
                longitude = it.longitude,
                canUse = it.canUse,
                seq = it.seq,
            )
        }
        realm.close()
        return copiedData
    }

    // 데이터 UI에 표시 (READ 모드)
    private fun displayData(data: FamilyPlace?) {
        if (::binding.isInitialized) {
            data?.let {
                // 데이터 UI에 적용
                binding.readModeNickname.text = it.name
                binding.readModeAddress.text = it.address
            }
        } else {
            // binding이 초기화되지 않은 경우에 대한 처리
            Log.e("MyFragment", "Binding is not initialized.")
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

        // 토글버튼
        binding.placeStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 토글 버튼이 꺼진 상태 (안전 상태)
                binding.dangerTextView.visibility = View.GONE
                binding.safeTextView.visibility = View.VISIBLE
            } else {
                // 토글 버튼이 켜진 상태 (위험 상태)
                binding.dangerTextView.visibility = View.VISIBLE
                binding.safeTextView.visibility = View.GONE
            }
        }

        val deviceStateService = DeviceStateService()

        // 등록 버튼 눌렀을 때
        binding.meetingPlaceAddSubmit.setOnClickListener {
            // 닉네임 설정
            val nickname = binding.meetingPlaceNickname.text.toString()
            tempFamilyPlace?.name = nickname

            if (nickname.isEmpty()) {
                Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 함수 실행 중단
            }

            if (tempFamilyPlace?.address.isNullOrEmpty() && tempFamilyPlace?.name.isNullOrEmpty()) {
                Toast.makeText(context, "주소를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 모드에 따라 바뀜
            when (mode) {
                Mode.READ -> {
                    // EDIT 모드로 전환
                    mode = Mode.EDIT

                    // EDIT 모드에 맞게 UI 업데이트
                    binding.meetingPlaceAddSubmit.text = "장소 수정"
                    binding.addEditContentWrap.visibility = View.VISIBLE
                    binding.readContentWrap.visibility = View.GONE
                }

                Mode.ADD -> {
                    if(deviceStateService.isNetworkAvailable(requireContext())){
                        addNewPlace(seqNumber)
                        dismiss()
                    }else{
                        Toast.makeText(context, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                Mode.EDIT -> {
                    if(deviceStateService.isNetworkAvailable(requireContext())){
                        updatePlace(seqNumber)
                        dismiss()
                    }else{
                        Toast.makeText(context, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                else -> {
                    Log.d("FamilyPlaceError", "어랏? 여기 들어오면 안되는 건데")
                }
            }

        }

        binding.meetingPlaceAddCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    // 장소 정보 업데이트 (EDIT 모드)
    private fun updatePlace(seq: Int?) {
        val memberId = getMemberId()

        //업데이트 로직 구현
        seq.let {
            tempFamilyPlace?.let { familyPlace ->
//                familyAPI.getFamilyUpdatePlaceInfo(

//                )
            }
        }

    }

    // 새로운 장소를 Realm에 추가하는 메서드
    private fun addNewPlace(seq: Int) {
        // 서버에 먼저 보내고, placeId 얻어온 다음에 Realm 저장 진행해야됨!!!
        val memberId = getMemberId()

        seq.let {
            tempFamilyPlace?.let { place ->
                // FamilyService의 인스턴스를 사용하여 함수 호출
                familyAPI.registFamilyPlace(
                    memberId,
                    place.name,
                    place.latitude,
                    place.longitude,
                    seq,
                    place.address, object : FamilyAPI.RegistFamilyCallback {
                        override fun onSuccess(result: PlaceDetailInfo) {
                            Log.i("placeId", result.toString())
                            saveFamilyPlaceToRealm(
                                result.placeId,
                                place.name,
                                place.address,
                                place.latitude,
                                place.longitude,
                                seq,
                            )
                            // 저장 뒤 업데이트 요청

                            familyFragment.fetchAll()
                        }

                        override fun onFailure(error: String) {
                            // 실패 시의 처리
                            Log.d("Family", "ADD MODE failed: $error")
                        }
                    })
            }
        }
    }

    private fun getMemberId(): String {
        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val memberId = realm.query<Member>().first().find()?.memberId ?: ""
        realm.close()
        return memberId
    }

    // Realm에 저장하는 코드 (ADD 모드)
    private fun saveFamilyPlaceToRealm(
        placeId: Int,
        name: String,
        address: String,
        lat: Double,
        lon: Double,
        seq: Int
    ) {
        // Realm 인스턴스 열기
        val realm = Realm.open(GoodNewsApplication.realmConfiguration)

        realm.writeBlocking {
            // 새로운 FamilyPlace 객체 생성 및 속성 설정
            copyToRealm(FamilyPlace().apply {
                this.placeId = placeId // 서버 응답에서 받은 placeId 사용
                this.name = name
                this.address = address
                this.latitude = lat
                this.longitude = lon
                this.seq = seq
            })
        }

        realm.close()
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
            Mode.ADD -> {
                binding.meetingPlaceAddSubmit.text = "장소 등록"
                binding.meetingPlaceMapView.visibility = View.VISIBLE
                binding.addEditContentWrap.visibility = View.VISIBLE
                binding.readContentWrap.visibility = View.GONE

            }

            Mode.EDIT -> {
                binding.meetingPlaceAddSubmit.text = "장소 수정"
                binding.meetingPlaceMapView.visibility = View.VISIBLE
                binding.addEditContentWrap.visibility = View.VISIBLE
                binding.readContentWrap.visibility = View.GONE
            }

            else -> { // READ 모드
                binding.meetingPlaceAddSubmit.text = "수정하기"
                binding.meetingPlaceMapView.visibility = View.GONE
                binding.addEditContentWrap.visibility = View.GONE
                binding.readContentWrap.visibility = View.VISIBLE
            }
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

                    // tempFamilyPlace에 저장
                    tempFamilyPlace = FamilyPlace().apply {
                        this.address = place.address?.toString() ?: ""
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