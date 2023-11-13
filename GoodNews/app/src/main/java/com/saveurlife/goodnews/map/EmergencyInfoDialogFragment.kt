package com.saveurlife.goodnews.map

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.style.CharacterStyle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentEmergencyInfoDialogBinding
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.service.UserDeviceInfoService
import io.realm.kotlin.Realm
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EmergencyInfoDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentEmergencyInfoDialogBinding
    private lateinit var inputText: String
    private var isSafe: String = ""

    // 위치 정보 초기화
    private var currLatitude: Double = 0.0
    private var currLongitude: Double = 0.0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emergencyAddSubmit.setOnClickListener {
            inputText = binding.locationTextView.text.toString()
            isSafe = if (binding.safeTextView.visibility == View.VISIBLE) {
                "0" // 안전
            } else {
                "1" // 위험
            }
            saveEmergencyInfoToRealm()
        }
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
            // 서버로 전송하는 코드 작성 필요 @@ + Toast로 알림도 필요한가?

            dismiss() // 다이얼로그 닫기
        }

        // 취소 버튼 클릭했을 때
        binding.emergencyAddCancel.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }

        return binding.root
    }

    private fun saveEmergencyInfoToRealm() {

        var currentTime = RealmInstant.now()

        // 현재 정보 realm에 저장
        CoroutineScope(Dispatchers.IO).launch {
            val realm = Realm.open(GoodNewsApplication.realmConfiguration)
            try {
                realm.write {
                    copyToRealm(MapInstantInfo().apply {

                        content = inputText
                        state = isSafe
                        latitude = (currLatitude * 10000).toInt() / 10000.0
                        longitude = (currLongitude * 10000).toInt() / 10000.0
                        time = currentTime
                    })
                }
                withContext(Dispatchers.Main) {
                    // UI 스레드에서 성공 메시지 표시
                    Toast.makeText(context, "위험 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("EmergencyInfoDialogFragment", "긴급 정보를 Realm에 저장하는 과정에서 오류", e)
            } finally {
                realm.close()
            }
        }
    }
}