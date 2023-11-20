package com.saveurlife.goodnews.map

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentOtherUserInfoBinding

class OtherUserInfoFragment : DialogFragment() {
    private lateinit var binding: FragmentOtherUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherUserInfoBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 채팅하기 버튼 클릭했을 때
        binding.chatMoveButton.setOnClickListener {
            // 채팅으로 이동 추가 @@

            dismiss()
        }

        return binding.root
    }

}