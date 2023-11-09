package com.saveurlife.goodnews.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleAdvertiseAdapter
import com.saveurlife.goodnews.ble.BleConnectedAdapter
import com.saveurlife.goodnews.ble.BleMeshAdvertiseData
import com.saveurlife.goodnews.ble.BleMeshConnectedUser

class MainAroundAdvertiseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_main_around_advertise, container, false)


        val bleMeshConnectedUser = listOf(
            BleMeshAdvertiseData("김싸피", R.drawable.baseline_person_24),
            BleMeshAdvertiseData("이싸피", R.drawable.baseline_person_24),
            BleMeshAdvertiseData("정싸피", R.drawable.baseline_person_24),
            // ... 추가 객체들
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMainAroundAdvertise)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BleAdvertiseAdapter(bleMeshConnectedUser)

        //구분선 넣기
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager(context).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        return view
    }
}