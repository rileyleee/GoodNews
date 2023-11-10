package com.saveurlife.goodnews.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleAdvertiseAdapter
import com.saveurlife.goodnews.ble.BleMeshAdvertiseData
import com.saveurlife.goodnews.common.SharedViewModel

class MainAroundAdvertiseFragment : Fragment() {
//    private lateinit var adapter: BleAdvertiseAdapter
    private lateinit var recyclerView: RecyclerView
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_around_advertise, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMainAroundAdvertise)
        //RecyclerView의 레이아웃 매니저를 LinearLayoutManager로 설정 - 항목들이 선형으로 배치
        recyclerView.layoutManager = LinearLayoutManager(context)

        // SharedViewModel의 LiveData를 관찰하고 어댑터를 업데이트합니다.
//        sharedViewModel.bleDeviceNames.observe(viewLifecycleOwner) { deviceNames ->
//            val bleMeshAdvertiseDataList = deviceNames.map { name ->
//                BleMeshAdvertiseData(name, R.drawable.baseline_person_24)
//            }
//            recyclerView.adapter = BleAdvertiseAdapter(bleMeshAdvertiseDataList)
//        }

        sharedViewModel.bleDevices.observe(viewLifecycleOwner) { bleDevices ->
            recyclerView.adapter = BleAdvertiseAdapter(bleDevices, sharedViewModel)
        }


        //구분선 넣기
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager(context).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        return view
    }
}