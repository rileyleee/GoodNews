package com.saveurlife.goodnews.map

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.ble.adapter.BleConnectedAdapter
import com.saveurlife.goodnews.chatting.ChattingDetailActivity
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.FragmentOtherUserInfoBinding

class OtherUserInfoFragment : DialogFragment() {
    private lateinit var binding: FragmentOtherUserInfoBinding

    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var userStatus: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherUserInfoBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 데이터 받아와 화면에 표시
        arguments?.let { bundle ->
            userName = bundle.getString("userName").toString()
            userStatus = bundle.getString("userStatus").toString()
            val distance = bundle.getDouble("distance")
            userId = bundle.getString("userId").toString()

            binding.otherNameTextView.text = userName
            binding.otherDistanceTextView.text = distance.toString() + "M"

            // 상태에 따라 상태 ui 색상 변경
            when (userStatus) {
                "safe" -> context?.let { ctx ->
                    this.binding.otherStatusCircle.backgroundTintList =
                        ContextCompat.getColorStateList(ctx, R.color.safe)
                }


                "injure" -> context?.let { ctx ->
                    this.binding.otherStatusCircle.backgroundTintList =
                        ContextCompat.getColorStateList(ctx, R.color.caution)
                }

                "death" -> context?.let { ctx ->
                    this.binding.otherStatusCircle.backgroundTintList =
                        ContextCompat.getColorStateList(ctx, R.color.black)
                }

                else -> {

                }
            }
        }

        // 채팅하기 버튼 클릭했을 때
        binding.chatMoveButton.setOnClickListener {
            val intent = Intent(context, ChattingDetailActivity::class.java).apply {
                putExtra("chatRoomId", userId)
                putExtra("chatName", userName)
                putExtra("chatOtherStatus", userStatus)
                putExtra("page",2)
            }
            startActivity(intent)



            dismiss()
        }

        return binding.root
    }

}