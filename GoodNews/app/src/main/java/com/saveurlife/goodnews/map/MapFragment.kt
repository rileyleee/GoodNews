package com.saveurlife.goodnews.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyBinding
import com.saveurlife.goodnews.databinding.FragmentMapBinding
import com.saveurlife.goodnews.family.FamilyAddFragment

class MapFragment : Fragment() {

    // 임시 코드
    private lateinit var binding: FragmentMapBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 임시 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 임시 버튼 클릭했을 때
        binding.emergencyAddButton.setOnClickListener {
            showEmergencyDialog()
        }
    }

    // 임시 코드
    private fun showEmergencyDialog() {
        val dialogFragment = EmergencyInfoDialogFragment()
        dialogFragment.show(childFragmentManager, "EmergencyInfoDialogFragment")
    }
}