package com.saveurlife.goodnews.map

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.databinding.FragmentMinimapDialogBinding

class MiniMapDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentMinimapDialogBinding


    override fun onViewCreated(view: View, userLocation: Bundle?) {
        super.onViewCreated(view, userLocation)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMinimapDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 미니맵 닫기 버튼 눌렀을 때
        binding.minimapClose.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    companion object {

    }
}