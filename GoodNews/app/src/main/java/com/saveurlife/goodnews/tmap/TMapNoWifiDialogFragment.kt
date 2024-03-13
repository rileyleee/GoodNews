package com.saveurlife.goodnews.tmap

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentTMapNoWifiDialogBinding

class TMapNoWifiDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentTMapNoWifiDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTMapNoWifiDialogBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.noWifiButton.setOnClickListener {
            dialog?.dismiss()
        }

        return binding.root
    }

}