package com.saveurlife.goodnews.map

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyUserInfoBinding

class FamilyUserInfoFragment : DialogFragment() {
    private lateinit var binding: FragmentFamilyUserInfoBinding

    private lateinit var userName: String
    private lateinit var userStatus: String
    private lateinit var userUpdateTime: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamilyUserInfoBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        // 데이터 받아와 화면에 표시
        arguments?.let { bundle ->
            userName = bundle.getString("userName").toString()
            userStatus = bundle.getString("userStatus").toString()
            userUpdateTime = bundle.getString("userUpdateTime").toString()

            binding.familyNameTextView.text = userName
            binding.familyLastTextView.text = userUpdateTime

            // 상태에 따라 상태 ui 색상 변경
            when (userStatus) {
                "safe" -> context?.let { ctx ->
                    this.binding.familyStatusCircle.backgroundTintList =
                        ContextCompat.getColorStateList(ctx, R.color.safe)
                }


                "injury" -> context?.let { ctx ->
                    this.binding.familyStatusCircle.backgroundTintList =
                        ContextCompat.getColorStateList(ctx, R.color.caution)
                }

                "death" -> context?.let { ctx ->
                    this.binding.familyStatusCircle.backgroundTintList =
                        ContextCompat.getColorStateList(ctx, R.color.death)
                }

                else -> {

                }
            }
        }

        // 닫기 버튼 클릭했을 때
        binding.familyCloseButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}