package com.saveurlife.goodnews.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.FragmentMainFamilyAroundListBinding

class MainFamilyAroundListFragment : Fragment() {
    private lateinit var binding: FragmentMainFamilyAroundListBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainFamilyAroundListBinding.inflate(inflater, container, false)

        // sharedViewModel의 isMainAroundVisible 관찰
        sharedViewModel.isMainAroundVisible.observe(viewLifecycleOwner, Observer { isVisible ->
            updateUI(isVisible, sharedViewModel.bleMeshConnectedDevicesMapLiveData.value.isNullOrEmpty())
        })

        // sharedViewModel의 bleMeshConnectedDevicesMapLiveData 관찰
        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(viewLifecycleOwner, Observer { devicesMap ->
            updateUI(sharedViewModel.isMainAroundVisible.value ?: true, devicesMap.isNullOrEmpty())
        })



        return binding.root
    }

    private fun updateUI(isMainAroundVisible: Boolean, isDevicesListEmpty: Boolean) {
        if (isMainAroundVisible) {
            binding.familyAroundInfo.visibility = View.VISIBLE
            binding.familyAroundImage.visibility = View.VISIBLE
        } else {
            binding.familyAroundInfo.visibility = View.GONE
            binding.familyAroundImage.visibility = View.GONE

            if (isDevicesListEmpty) {
                // 리스트가 비어있을 때
                binding.familyLottieBle.visibility = View.VISIBLE
                binding.familyAroundInfoConnect.visibility = View.VISIBLE
//                binding.recyclerViewMainAroundList.visibility = View.GONE
            } else {
                // 리스트가 있을 때
                binding.familyLottieBle.visibility = View.GONE
                binding.familyAroundInfoConnect.visibility = View.GONE
//                binding.recyclerViewMainAroundList.visibility = View.VISIBLE
            }
        }
    }
}