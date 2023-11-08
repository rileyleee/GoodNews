package com.saveurlife.goodnews.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleConnectedAdapter
import com.saveurlife.goodnews.ble.BleMeshConnectedUser


class MainAroundListFragment : Fragment() {
//    private val bleMeshConnectedUser = BleMeshConnectedUser("1","이름", "2023", "1", 38.000,127.00);

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_around_list, container, false)

        //데이터 추가
        val bleMeshConnectedUser = listOf(
            BleMeshConnectedUser("1", "이름1", "2023", "1", 38.000, 127.00),
            BleMeshConnectedUser("2", "이름2", "2023", "2", 39.000, 128.00),
            BleMeshConnectedUser("3", "이름3", "2023", "3", 39.000, 128.00),
            BleMeshConnectedUser("4", "이름4", "2023", "4", 39.000, 128.00),
            BleMeshConnectedUser("5", "이름1", "2023", "1", 38.000, 127.00),
            BleMeshConnectedUser("6", "이름2", "2023", "2", 39.000, 128.00),
            BleMeshConnectedUser("7", "이름3", "2023", "3", 39.000, 128.00),
            BleMeshConnectedUser("8", "이름4", "2023", "4", 39.000, 128.00),
            // ... 추가 객체들
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMainAroundList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BleConnectedAdapter(bleMeshConnectedUser)

        //구분선 넣기
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager(context).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        return view
    }
}