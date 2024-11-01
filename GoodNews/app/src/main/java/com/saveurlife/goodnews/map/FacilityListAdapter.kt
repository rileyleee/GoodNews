package com.saveurlife.goodnews.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ItemListFacilityBinding
import com.saveurlife.goodnews.main.PreferencesUtil
import com.saveurlife.goodnews.models.OffMapFacility
import com.saveurlife.goodnews.sync.TimeService

class FacilityListAdapter(
    private var facilities: List<OffMapFacility>,
    private val preferencesUtil: PreferencesUtil
) :
    RecyclerView.Adapter<FacilityListAdapter.FacilityViewHolder>() {
    class FacilityViewHolder(private val binding: ItemListFacilityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(facility: OffMapFacility, preferencesUtil: PreferencesUtil) {
            binding.facilityListName.text = facility.name
            binding.facilityListType.text = facility.type

            val iconRes = when (facility.type) {
                "대피소" -> R.drawable.ic_shelter
                "병원" -> R.drawable.ic_hospital
                "편의점", "마트" -> R.drawable.ic_grocery
                "가족" -> R.drawable.ic_family
                "약속장소" -> R.drawable.ic_meeting_place
                else -> R.drawable.ic_pin
            }
            binding.facilityIconType.setBackgroundResource(iconRes)

            // 임시 default 값 -> 마지막 업데이트 시각
            val lastUpdateTime = preferencesUtil.getLong("SyncTime", 0L)
            val timeService = TimeService()
            binding.facilityLastUpdateTime.text =
                timeService.convertDateLongToString(lastUpdateTime)

            // 사용 가능 여부
            when (facility.canUse) {
                true -> {
                    binding.useTrueWrap.visibility = View.VISIBLE
                    binding.useFalseWrap.visibility = View.GONE
                }

                false -> {
                    binding.useTrueWrap.visibility = View.GONE
                    binding.useFalseWrap.visibility = View.VISIBLE
                }
            }
        }
    }

    // 리스트 업데이트
    fun updateData(newFacilities: List<OffMapFacility>) {
        facilities = newFacilities // 새로운 리스트로 교체
        notifyDataSetChanged() // 리스트를 새로고침합니다.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val binding =
            ItemListFacilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FacilityViewHolder(binding)
    }

    override fun getItemCount() = facilities.size

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        val facility = facilities[position]
        holder.bind(facility, preferencesUtil)
    }
}