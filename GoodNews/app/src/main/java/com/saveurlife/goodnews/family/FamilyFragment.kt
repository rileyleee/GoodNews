package com.saveurlife.goodnews.family


import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
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
import com.saveurlife.goodnews.sync.FamilySyncWorker
import com.saveurlife.goodnews.sync.SyncService
import kotlinx.coroutines.delay

class FamilyFragment : Fragment(), FamilyListAdapter.OnItemClickListener {
    enum class Mode { ADD, READ, EDIT }

    private lateinit var familyListRecyclerView: RecyclerView
    private lateinit var binding: FragmentFamilyBinding
    private lateinit var realm: Realm
    lateinit var familyListAdapter: FamilyListAdapter

    // 클래스 레벨 변수로 장소 데이터 저장
    private var familyPlaces: List<FamilyPlace> = listOf()

    private lateinit var deviceStateService: DeviceStateService
    private lateinit var userDeviceInfoService: UserDeviceInfoService
    private var familyAPI: FamilyAPI = FamilyAPI()
    private var memberAPI: MemberAPI = MemberAPI()
    private lateinit var memberId:String

    companion object{
        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        lateinit var familyEditText:TextView
        var numToStatus:Map<Int, Status> = mapOf(
            0 to Status.HEALTHY,
            1 to Status.INJURED,
            2 to Status.DECEASED,
            3 to Status.NOT_SHOWN
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        Log.d("test", "실행됨")
        super.onResume()
            var workManager = WorkManager.getInstance(requireContext())

            // 조건 설정 - 인터넷 연결 시에만 실행
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // request 생성
            val updateRequest = OneTimeWorkRequest.Builder(FamilySyncWorker::class.java)
                .setConstraints(constraints)
                .build()


        // WorkManager 작업 완료 후 호출되는 리스너를 등록
//        workManager.getWorkInfoByIdLiveData(updateRequest.id)
//            .observe(viewLifecycleOwner) {
//                if (it != null && it.state == WorkInfo.State.SUCCEEDED) {
//                    // WorkManager 작업이 완료되면 UI 갱신
                    Log.d("testtt","여긴 워커 끝")
                    addList()

//                    familyListAdapter.notifyDataSetChanged()
//                }
//            }
            // 실행
            workManager.enqueue(updateRequest)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        deviceStateService = DeviceStateService()
        familyListAdapter = FamilyListAdapter(context, this)
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
        loadFamilyPlaces()

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


        familyListAdapter = FamilyListAdapter(requireContext(), this)
        familyListRecyclerView.adapter = familyListAdapter

    }

    // 아이템 클릭 이벤트 처리
    @SuppressLint("NotifyDataSetChanged")
    override fun onAcceptButtonClick(position: Int) {
        val item = familyListAdapter.familyList[position]
        // 서버 요청 등 처리
        familyAPI.updateRegistFamily(item.acceptNumber, false)
//        addList()
//        familyListAdapter.notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRejectButtonClick(position: Int) {
        val item = familyListAdapter.familyList[position]
        // 서버 요청 등 처리
        familyAPI.updateRegistFamily(item.acceptNumber, true)
//        addList()
//        familyListAdapter.notifyDataSetChanged()
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


    // 가족 추가
    private fun showAddDialog() {
        val dialogFragment = FamilyAddFragment(this)
        if(deviceStateService.isNetworkAvailable(requireContext())){
            dialogFragment.show(childFragmentManager, "FamilyAddFragment")
        }else{
            Toast.makeText(requireContext(), "네트워크 상태가 불안정합니다.\n다시 시도해 주세요", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun addList(){

        // 서버에서 리스트 가져와서 추가 -> 인터넷 연결 시

        if(deviceStateService.isNetworkAvailable(requireContext())){
            val userDeviceInfoService = UserDeviceInfoService(requireContext())
            familyListAdapter.resetFamilyList()

            familyAPI.getRegistFamily(userDeviceInfoService.deviceId, object : FamilyAPI.WaitListCallback {
                override fun onSuccess(result: ArrayList<WaitInfo>) {
                    result.forEach{
                        var str = it.name
                        var cov = ""
                        if (str.length == 3) {
                            cov = it.name[0] + "*" + it.name[2]
                        } else if (str.length == 2) {
                            cov = it.name[0] + "*"
                        } else {
                            cov = it.name[0].toString()
                            for (i in 2 until str.length) {
                                cov += "*"
                            }
                        }

                        familyListAdapter.addFamilyWait(cov, it.id)
                        familyListAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(error: String) {
                    // 실패 시의 처리
                    Log.d("Family", "Registration failed: $error")
                }
            })
        }

        Log.d("testt", "여기 실행은 되는건가")
        val resultRealm = FamilyFragment.realm.query<FamilyMemInfo>().find()
        val syncService = SyncService()
        Log.d("testtt", resultRealm.toString())
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