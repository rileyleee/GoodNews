package com.saveurlife.goodnews.flashlight

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class FlashlightFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_flashlight, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 손전등 테스트 데이터
        var recordTestData = arrayListOf(
            FlashlightData(FlashType.SELF, "Record 1", "... --- ..."),
            FlashlightData(FlashType.OTHER, "Record 2", "-- .- .. -."),
            FlashlightData(FlashType.SELF, "Record 3", ".- .-. ."),
            FlashlightData(FlashType.SELF, "Record 4", ".- .-. .")
        )
        var flashListManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        var flashListAdapter = FlashlightListAdapter(recordTestData)


        var flashRecordListManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        var flashRecordListAdapter = FlashlightRecordAdapter(recordTestData)

        view.findViewById<RecyclerView>(R.id.flashList).apply {
            adapter = flashListAdapter
            layoutManager = flashListManager
        }

        view.findViewById<RecyclerView>(R.id.flashRecordList).apply {
            adapter = flashRecordListAdapter
            layoutManager = flashRecordListManager
        }
    }
}