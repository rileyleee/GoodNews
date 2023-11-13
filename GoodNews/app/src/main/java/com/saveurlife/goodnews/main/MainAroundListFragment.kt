package com.saveurlife.goodnews.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleConnectedAdapter
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.FragmentFamilyAlarmBinding
import com.saveurlife.goodnews.databinding.FragmentMainAroundListBinding


class MainAroundListFragment : Fragment() {
//    private val bleMeshConnectedUser = BleMeshConnectedUser("1","이름", "2023", "1", 38.000,127.00);
    private lateinit var binding: FragmentMainAroundListBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainAroundListBinding.inflate(inflater, container, false)

        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(viewLifecycleOwner, Observer { connectedDevicesMap ->
            // connectedDevicesMap에서 필요한 데이터 추출
            val users = connectedDevicesMap.flatMap { it.value.values }.toList()
            val adapter = BleConnectedAdapter(users)
            binding.recyclerViewMainAroundList.adapter = adapter
        })

        //데이터 추가
        val bleMeshConnectedUser = listOf(
            BleMeshConnectedUser("1", "이름1", "2023", "1", 38.000, 127.00),
            BleMeshConnectedUser("2", "이름2", "2023", "2", 39.000, 128.00),
            // ... 추가 객체들
        )

        val recyclerView = binding.recyclerViewMainAroundList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BleConnectedAdapter(bleMeshConnectedUser)

        //구분선 넣기
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager(context).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        return binding.root
    }
}