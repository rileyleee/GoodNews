package com.saveurlife.goodnews.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.databinding.FragmentFamilyAlarmBinding
import com.saveurlife.goodnews.databinding.FragmentMyStatusDialogBinding

class MyStatusDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentMyStatusDialogBinding

    private var listener: StatusSelectListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is StatusSelectListener) {
            listener = parentFragment as StatusSelectListener
        } else {
            throw RuntimeException("$parentFragment must implement StatusSelectListener")
        }
        println("Attached context class: ${context.javaClass.simpleName}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyStatusDialogBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.myStatusCancle.setOnClickListener{
            dismiss()
        }


        binding.safeCircle.setOnClickListener {
            listener?.onStatusSelected("safe")
            dismiss()
        }
        binding.injuryCircle.setOnClickListener {
            listener?.onStatusSelected("injury")
            dismiss()
        }
        binding.deathCircle.setOnClickListener {
            listener?.onStatusSelected("death")
            dismiss()
        }
        binding.unknownCircle.setOnClickListener {
            listener?.onStatusSelected("unknown")
            dismiss()
        }

        return binding.root
    }

    interface StatusSelectListener {
        fun onStatusSelected(status: String)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}