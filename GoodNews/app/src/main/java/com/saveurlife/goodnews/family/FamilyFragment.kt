package com.saveurlife.goodnews.family


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.api.WaitInfo
import com.saveurlife.goodnews.databinding.FragmentFamilyBinding
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.service.DeviceStateService
import com.saveurlife.goodnews.models.FamilyPlace
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import com.saveurlife.goodnews.service.UserDeviceInfoService
import com.saveurlife.goodnews.sync.FamilySync
import com.saveurlife.goodnews.sync.SyncService

class FamilyFragment : Fragment(), FamilyListAdapter.OnItemClickListener {
    enum class Mode { ADD, READ, EDIT }

    private lateinit var familyListRecyclerView: RecyclerView
    private lateinit var binding: FragmentFamilyBinding
    private lateinit var realm: Realm
    lateinit var familyListAdapter: FamilyListAdapter

    // 클래스 레벨 변수로 장소 데이터 저장
    private val familyPlace: MutableLiveData<List<FamilyPlace>> by lazy {
        MutableLiveData<List<FamilyPlace>>()
    }


    private lateinit var deviceStateService: DeviceStateService
    private lateinit var userDeviceInfoService: UserDeviceInfoService
    private var familyAPI: FamilyAPI = FamilyAPI()
    private lateinit var memberId:String
    private lateinit var familySync: FamilySync

    companion object{
        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        lateinit var familyEditText:TextView
        var numToStatus:Map<Int, Status> = mapOf(
            1 to Status.HEALTHY,
            2 to Status.INJURED,
            3 to Status.DECEASED,
            0 to Status.NOT_SHOWN
        )
    }
    override fun onResume() {
        super.onResume()

        familySync = FamilySync(requireContext())
        val deviceStateService = DeviceStateService()
        // 인터넷 연결 시에만 실행함
        val observer1 = Observer<Boolean>{
            if(it){
                addPlaceList()
                familySync.familyPlaceUpdated.value = false
                updatedUIWithFamilyPlaces()
            }
        }

        val observer2 = Observer<Boolean>{
            if(it){
                addList()
                familySync.familyMemInfoUpdated.value = false
            }
        }
        familySync.familyPlaceUpdated.observe(this, observer1)
        familySync.familyMemInfoUpdated.observe(this, observer2)
        if(deviceStateService.isNetworkAvailable(requireContext())){
            // 초기에 갱신도 해야됨.
            familySync.fetchFamily()
        }
        addList()
        addPlaceList()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        deviceStateService = DeviceStateService()
        familyListAdapter = FamilyListAdapter(this)
        userDeviceInfoService = UserDeviceInfoService(requireContext())
        memberId = userDeviceInfoService.deviceId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyBinding.inflate(inflater, container, false)
        familyEditText = binding.familyEditText
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 로딩, UI 업데이트
        // 각 장소 클릭 이벤트 처리
        binding.meetingPlaceFirst.setOnClickListener { handlePlaceClick(seq = 1) }
        binding.meetingPlaceSecond.setOnClickListener { handlePlaceClick(seq = 2) }
        binding.meetingPlaceThird.setOnClickListener { handlePlaceClick(seq = 3) }

        // 가족 신청 버튼을 클릭 했을 때
        binding.familyAddButton.setOnClickListener {
            showAddDialog()
        }

        // 리사이클러뷰 연결
        familyListRecyclerView = view.findViewById(R.id.familyList)
        familyListRecyclerView.layoutManager = LinearLayoutManager(context)

        familyListAdapter = FamilyListAdapter(this)
        familyListRecyclerView.adapter = familyListAdapter
    }

    // 아이템 클릭 이벤트 처리
    override fun onAcceptButtonClick(position: Int) {
        val item = familyListAdapter.familyList[position]
        // 인터넷 연결 되어 있다면 - 어짜피 연결 되어있을때만 들어오긴 함
        if(deviceStateService.isNetworkAvailable(requireContext())){
            familyAPI.updateRegistFamily(item.acceptNumber, false)
        }
        //내 리스트에 넣긴 해야함.
        val observer2 = Observer<Boolean>{
            if(it){
                addList()
                familySync.familyMemInfoUpdated.value = false
            }
        }
        familySync.familyMemInfoUpdated.observe(this, observer2)
        familySync.fetchFamily()
    }

    override fun onRejectButtonClick(position: Int) {
        val item = familyListAdapter.familyList[position]
        // 인터넷 연결 되어 있다면 - 어짜피 연결 되어있을때만 들어오긴 함
        if(deviceStateService.isNetworkAvailable(requireContext())){
            familyAPI.updateRegistFamily(item.acceptNumber, false)
        }
        addList()
    }

    private fun updatedUIWithFamilyPlaces() {
        familyPlace.value?.forEach { place ->
            val statusColor = if (place.canUse) ContextCompat.getColor(
                requireContext(),
                R.color.safe
            ) else ContextCompat.getColor(requireContext(), R.color.danger)

            val statusText = if (place.canUse) "안전" else "위험"

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
        val place = familyPlace.value?.find { it.seq == seq }
        val mode = if (place == null) Mode.ADD else Mode.READ

        showMeetingDialog(seq, mode)
    }

    // 모달 창 띄워주는 것
    private fun showMeetingDialog(seq: Int, mode: Mode) {
        val observer3 = Observer<List<FamilyPlace>>{
            updatedUIWithFamilyPlaces()
        }
        familyPlace.observe(viewLifecycleOwner, observer3)


        val dialogFragment = FamilyPlaceAddEditFragment(this, requireContext()).apply {
            arguments = Bundle().apply {
                //데이터가 있으면 읽기 모드, 없으면 추가 모드
                putSerializable("mode", mode)
                putInt("seq", seq)
            }
        }
        dialogFragment.show(childFragmentManager, "FamilyPlaceAddEditFragment")
    }


    // 가족 추가
    private fun showAddDialog() {
        val dialogFragment = FamilyAddFragment(this)
        if(deviceStateService.isNetworkAvailable(requireContext())){
            dialogFragment.show(childFragmentManager, "FamilyAddFragment")
        }else{
            Toast.makeText(requireContext(), "네트워크 상태가 불안정합니다.\n다시 시도해 주세요", Toast.LENGTH_SHORT).show()
        }
    }


    private fun convertName(name:String): String {
        var covName = ""
        if (name.length == 3) {
            covName = name[0] + "*" + name[2]
        } else if (name.length == 2) {
            covName = name[0] + "*"
        } else {
            covName = name[0].toString()
            for (i in 2 until name.length) {
                covName += "*"
            }
        }
        return covName
    }
    fun fetchAll(){
        familySync.fetchFamily()
    }
    private fun addPlaceList(){
        realm = Realm.open(GoodNewsApplication.realmConfiguration)

        val resultRealm2 = FamilyFragment.realm.query<FamilyPlace>().find()
        if(resultRealm2 !=null){
            familyPlace.value = realm.copyFromRealm(resultRealm2)
            updatedUIWithFamilyPlaces()
        }
    }
    private fun addList(){
        // 서버에서 가족들 + 신청 리스트 가져오자

        if(deviceStateService.isNetworkAvailable(requireContext())){
            val userDeviceInfoService = UserDeviceInfoService(requireContext())
            familyListAdapter.resetFamilyList()

            // 신청 리스트 가져오기
            familyAPI.getRegistFamily(userDeviceInfoService.deviceId, object : FamilyAPI.WaitListCallback {
                override fun onSuccess(result: ArrayList<WaitInfo>) {
                    result.forEach{
                        familyListAdapter.addFamilyWait(convertName(it.name), it.id)
                        familyListAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(error: String) {
                    // 실패 시의 처리
                    Log.d("Family", "Registration failed: $error")
                }
            })
            
        }
        // 가족 리스트 가져오기
        val resultRealm = FamilyFragment.realm.query<FamilyMemInfo>().find()
        val syncService = SyncService()

        // 페이지 오면 기존 realm에꺼 추가(이땐 이미 동기화 된 시점임)
        if (resultRealm != null) {
            resultRealm.forEach {
                if(it.state == null){
                    familyListAdapter.addFamilyInfo(it.name, Status.NOT_SHOWN, syncService.realmInstantToString(it.lastConnection))
                }else{
                    familyListAdapter.addFamilyInfo(it.name, numToStatus[it.state!!.toInt()]!!, syncService.realmInstantToString(it.lastConnection))
                }
            }
        }

        familyListAdapter.notifyDataSetChanged()
    }
}