package com.saveurlife.goodnews.map

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.api.MapAPI
import com.saveurlife.goodnews.databinding.FragmentEmergencyInfoDialogBinding
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.service.DeviceStateService
import com.saveurlife.goodnews.sync.SyncService
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates


class EmergencyInfoDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentEmergencyInfoDialogBinding
    private lateinit var inputText: String
    private lateinit var currentLocationInfo: RealmResults<MapInstantInfo>
    private var isSafe: String = ""
    private var currentTime by Delegates.notNull<Long>()


    // 위치 정보 초기화
    private var currLatitude: Double = 0.0
    private var currLongitude: Double = 0.0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmergencyInfoDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        currLatitude = arguments?.getDouble("latitude", 0.0) ?: 0.0
        currLongitude = arguments?.getDouble("longitude", 0.0) ?: 0.0

        Log.v("정보 공유 창에 보낼 현재 위도", "$currLatitude")
        Log.v("정보 공유 창에 보낼 현재 경도", "$currLongitude")
        if (currLatitude == 0.0 && currLongitude == 0.0) {
            binding.myLocationTextView.text = "위치 정보가 없습니다."
        } else {
            binding.myLocationTextView.text = "위도 : $currLatitude / 경도 : $currLongitude"
        }
        // 토글 상태 변경 시 색상 업데이트
        binding.emergencyStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) { // 스위치가 안전 상태
                binding.dangerTextView.visibility = View.GONE
                binding.safeTextView.visibility = View.VISIBLE
            } else {
                binding.dangerTextView.visibility = View.VISIBLE
                binding.safeTextView.visibility = View.GONE
            }
        }

        // 등록 버튼 클릭했을 때
        binding.emergencyAddSubmit.setOnClickListener {
            inputText = binding.locationTextView.text.toString()
            isSafe = if (binding.safeTextView.visibility == View.VISIBLE) {
                "0" // 안전
            } else {
                "1" // 위험
            }
            saveEmergencyInfoToRealm()
        }

        // 취소 버튼 클릭했을 때
        binding.emergencyAddCancel.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }

        return binding.root
    }

    private fun saveEmergencyInfoToRealm() {


        // 업데이트 시각 보정(+9시간 처리-> 한국 시각)
        currentTime = System.currentTimeMillis()
        // currentTime += TimeUnit.HOURS.toMillis(9) (API 작업 시 이중 처리 됨에 따라 일단 주석)

        val timeRealmInstant = RealmInstant.from(currentTime / 1000, (currentTime % 1000).toInt())

        val sharedPref = GoodNewsApplication.preferences

        val userId = sharedPref.getString("id", "id를 찾을 수 없음")
        val modifiedTime = SimpleDateFormat("yyMMddHHmmss", Locale.getDefault()).format(Date(currentTime))
        val storedId = userId+modifiedTime

        // 현재 정보 realm에 저장
        CoroutineScope(Dispatchers.IO).launch {

            val realm = Realm.open(GoodNewsApplication.realmConfiguration)

            // 현재 위치에 대한 위험 정보 존재 여부 확인
            currentLocationInfo = realm.query<MapInstantInfo>("latitude = $0 AND longitude = $1", (currLatitude * 1000).toInt() / 1000.0, (currLongitude * 1000).toInt() / 1000.0).find()

            if (currentLocationInfo.isEmpty()) {
                Log.i("EmergencyInfoDialogFragment", "이 위치에 대한 위험 정보가 없어서 바로 저장합니다.")
                try {
                    realm.write {
                        copyToRealm(MapInstantInfo().apply {
                            id = storedId
                            content = inputText
                            state = isSafe
                            latitude = (currLatitude * 1000).toInt() / 1000.0
                            longitude = (currLongitude * 1000).toInt() / 1000.0
                            time = timeRealmInstant
                        })
                    }
                    val mapAPI = MapAPI()
                    val syncService = SyncService()
                    val deviceStateService = DeviceStateService()
                    if(deviceStateService.isNetworkAvailable(requireContext())){
                        if(isSafe =="1"){
                            mapAPI.registMapFacility(true, inputText, (currLatitude * 1000).toInt() / 1000.0, (currLongitude * 1000).toInt() / 1000.0, syncService.realmInstantToString(timeRealmInstant))
                        }else{
                            mapAPI.registMapFacility(false, inputText, (currLatitude * 1000).toInt() / 1000.0, (currLongitude * 1000).toInt() / 1000.0, syncService.realmInstantToString(timeRealmInstant))
                        }
                    }

                    withContext(Dispatchers.Main) {
                        // UI 스레드에서 성공 메시지 표시
                        Toast.makeText(context, "위험 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("EmergencyInfoDialogFragment", "긴급 정보를 Realm에 저장하는 과정에서 오류", e)
                } finally {
                    realm.close()
                    dismiss() // 다이얼로그 닫기
                }

            } else {
                Log.i("EmergencyInfoDialogFragment", "이 위치에 대한 위험 정보가 존재함에 따라 값을 수정합니다.")
                try {
                    realm.write {
                        // 트랜잭션 안에서 쿼리 조회한 라이브 데이터여야만 수정 가능해서 아래와 같이 재조회
                        val changedInfo = query<MapInstantInfo>("latitude = $0 AND longitude = $1", (currLatitude * 1000).toInt() / 1000.0, (currLongitude * 1000).toInt() / 1000.0).find().first()
                        changedInfo.id = storedId
                        changedInfo.content = inputText
                        changedInfo.state = isSafe
                        changedInfo.time = timeRealmInstant
                    }
                    // 데이터가 존재할 때 동기화 코드
                    // 여기에 작성

                    withContext(Dispatchers.Main) {
                        // UI 스레드에서 성공 메시지 표시
                        Toast.makeText(context, "위험 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("EmergencyInfoDialogFragment", "긴급 정보를 Realm에 저장하는 과정에서 오류", e)
                } finally {
                    realm.close()
                    dismiss() // 다이얼로그 닫기
                }
            }
        }
    }
}