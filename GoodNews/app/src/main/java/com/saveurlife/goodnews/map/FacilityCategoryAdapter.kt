package com.saveurlife.goodnews.map

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ItemCategoryFacilityBinding
import com.saveurlife.goodnews.models.FacilityUIType

class FacilityCategoryAdapter(
    private val categories: List<FacilityUIType>,
    private val onCategorySelected: (FacilityUIType) -> Unit
) :
    RecyclerView.Adapter<FacilityCategoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemCategoryFacilityBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var selectedPosition = categories.indexOf(FacilityUIType.ALL)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // LayoutInflater를 사용해 View Binding 인스턴스를 생성
        val binding =
            ItemCategoryFacilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.facilityTypeName.text = category.displayName

        // 현재 뷰 홀더의 실제 위치 가져오기
        val actualPosition = holder.adapterPosition

        // 강조 표시 로직
        holder.binding.root.isSelected = position == selectedPosition

        // "전체" 카테고리일 경우 아이콘을 숨깁니다.
        if (category == FacilityUIType.ALL) {
            holder.binding.facilityTypeIcon.visibility = View.GONE
        } else {
            holder.binding.facilityTypeIcon.visibility = View.VISIBLE
            holder.binding.facilityTypeIcon.setImageResource(getIconResource(category))
        }

        holder.binding.root.setOnClickListener {
            // 이전에 선택된 아이템 UI 업데이트
            notifyItemChanged(selectedPosition)
            // 새로운 아이템 선택
            selectedPosition = position
            notifyItemChanged(selectedPosition)

            onCategorySelected(category)
        }

        // 첫 번째 아이템 시작 여백 주기
        with(holder.itemView.layoutParams as RecyclerView.LayoutParams) {
            leftMargin = when (position) {
                0 -> TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    20f,
                    holder.itemView.context.resources.displayMetrics
                ).toInt()

                else -> 0
            }
        }
    }

    private fun getIconResource(facilityUIType: FacilityUIType): Int {
        return when (facilityUIType) {
            FacilityUIType.SHELTER -> R.drawable.ic_shelter
            FacilityUIType.HOSPITAL -> R.drawable.ic_hospital
            FacilityUIType.GROCERY -> R.drawable.ic_grocery
            FacilityUIType.FAMILY -> R.drawable.ic_family
            FacilityUIType.MEETING_PLACE -> R.drawable.ic_meeting_place
            else -> 0
        }
    }


}