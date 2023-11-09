package com.saveurlife.goodnews.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentHomeBinding
import com.saveurlife.goodnews.databinding.FragmentMyStatusBinding

class MyStatusFragment : Fragment(), MyStatusDialogFragment.StatusSelectListener {
    private lateinit var binding: FragmentMyStatusBinding

    private lateinit var preferencesUtil: PreferencesUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layer.setOnClickListener {
            val dialogFragment = MyStatusDialogFragment()
            dialogFragment.show(childFragmentManager, "MyStatusDialogFragment")
        }

        binding.myGroup.setOnClickListener {
            (activity as? MainActivity)?.switchToChattingFragment(1)
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
        binding = FragmentMyStatusBinding.inflate(inflater, container, false)
        preferencesUtil = PreferencesUtil(requireContext())

        val savedStatus = preferencesUtil.getString("status", "unknown")
        onStatusSelected(savedStatus)

        return binding.root
    }

}