package com.saveurlife.goodnews.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentHomeBinding
import com.saveurlife.goodnews.databinding.FragmentMyPageBinding

class HomeFragment : Fragment(), MyStatusDialogFragment.StatusSelectListener {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myStatusUpdateButtom.setOnClickListener {
            val dialogFragment = MyStatusDialogFragment()
            dialogFragment.show(childFragmentManager, "MyStatusDialogFragment")
        }
    }

    override fun onStatusSelected(status: String) {
        when (status) {
            "safe" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_safe_circle)
            "injury" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_injury_circle)
            "death" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_death_circle)
            "unknown" -> binding.myStatus.setBackgroundResource(R.drawable.my_status_circle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }
}