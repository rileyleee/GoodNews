package com.saveurlife.goodnews.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.databinding.ItemListFacilityBinding
import com.saveurlife.goodnews.models.OffMapFacility

class FacilityListAdapter(private val facilities: List<OffMapFacility>) :
    RecyclerView.Adapter<FacilityListAdapter.FacilityViewHolder>() {
    class FacilityViewHolder(private val binding: ItemListFacilityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(facility: OffMapFacility) {
            binding.facilityListName.text = facility.name
            binding.facilityListType.text = facility.type
            // 여기에 거리 및 업데이트 시간 표시 로직 추가
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val binding =
            ItemListFacilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FacilityViewHolder(binding)
    }

    override fun getItemCount() = facilities.size

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        val facility = facilities[position]
        holder.bind(facility)
    }
}