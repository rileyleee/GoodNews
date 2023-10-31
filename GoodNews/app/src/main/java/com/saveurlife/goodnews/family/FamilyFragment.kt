package com.saveurlife.goodnews.family

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.flashlight.FlashType
import com.saveurlife.goodnews.flashlight.FlashlightData

class FamilyFragment : Fragment() {

    private lateinit var familyListRecyclerView: RecyclerView
    private var dataListAdapter = listOf<FamilyData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_family, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var testData = listOf(
            FamilyData("김싸피", Status.HEALTHY, "2023-10-31 14:00:00"),
            FamilyData("이싸피", Status.INJURED, "2023-10-31 13:00:00"),
            FamilyData("싸피이", Status.DECEASED, "2023-10-30 15:00:00"),
            FamilyData("싸피싸피", Status.NOT_SHOWN, "2023-10-29 10:00:00")
        )

        familyListRecyclerView = view.findViewById(R.id.familyList)
        familyListRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = FamilyListAdapter(testData)
        familyListRecyclerView.adapter = adapter
    }
}