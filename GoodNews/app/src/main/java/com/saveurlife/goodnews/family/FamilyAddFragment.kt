package com.saveurlife.goodnews.family

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.databinding.FragmentFamilyAddBinding
import com.saveurlife.goodnews.service.UserDeviceInfoService


class FamilyAddFragment(private var familyFragment: FamilyFragment) : DialogFragment() {
    private lateinit var binding: FragmentFamilyAddBinding
    private val familyAPI = FamilyAPI()
    private lateinit var userDeviceInfoService:UserDeviceInfoService
    private lateinit var memberId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyAddBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDeviceInfoService = UserDeviceInfoService(requireContext())
        memberId = userDeviceInfoService.deviceId


        // 신청 버튼 클릭했을 때
        binding.familyAddSubmit.setOnClickListener {
            // 여기에 추가되어야 하는 것 : 신청을 눌렀을 때, 해당 신청 요청 box도 하나 띄워줘야 한다.
//            Toast.makeText(activity, "가족 신청 버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show()

            // 신청 요청
            var result = familyAPI.registFamily(memberId, FamilyFragment.familyEditText.text.toString() , object : FamilyAPI.FamilyRegistrationCallback {
                override fun onSuccess(result: String) {
                    Log.d("Family", result)
                    dismiss()
                }

                override fun onFailure(error: String) {
                    // 실패 시의 처리
                    Log.d("Family", "Registration failed: $error")
                    showAlertDialog(error)
                }
            })
        }

        // 취소 버튼 클릭했을 때
        binding.familyAddCancel.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
    }



    private fun showAlertDialog(text:String) {
        val currentActivity = activity

        if (currentActivity != null && isAdded) {
            // Context와 Fragment의 상태를 체크
            AlertDialog.Builder(currentActivity)
                .setTitle("알림")
                .setMessage("가족신청을 실패했습니다.\n다시 확인해 주세요!\nError:${text}")
                .setPositiveButton("확인") { _, _ ->
                    // 다이얼로그를 닫기
                    dismiss()
                }
                .create()
                .show()
        } else {
            Log.e("FamilyAddFragment", "Activity is null or fragment is not added.")
        }
    }
}