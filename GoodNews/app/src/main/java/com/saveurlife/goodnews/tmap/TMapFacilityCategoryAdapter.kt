package com.saveurlife.goodnews.tmap

import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ItemCategoryFacilityBinding

class TMapFacilityCategoryAdapter (private val categories: List<TMapFacilityUiType>,
                                   private val onCategorySelected: (TMapFacilityUiType) -> Unit) :  RecyclerView.Adapter<TMapFacilityCategoryAdapter.ViewHolder>(){

    class ViewHolder(val binding: ItemCategoryFacilityBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var selectedPosition = categories.indexOf(TMapFacilityUiType.ALL)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryFacilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]

        // 강조 표시 로직
        holder.binding.root.isSelected = position == selectedPosition

        holder.binding.facilityTypeName.text = category.displayName

        // "전체" 카테고리일 경우 아이콘을 숨깁니다.
        if (category == TMapFacilityUiType.ALL) {
            holder.binding.facilityTypeIcon.visibility = View.GONE
        } else {
            holder.binding.facilityTypeIcon.visibility = View.VISIBLE
            holder.binding.facilityTypeIcon.setImageResource(getIconResource(category))
        }

        holder.binding.root.setOnClickListener {
            // 현재 뷰 홀더의 위치를 얻음
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            // 이전에 선택된 아이템의 상태 업데이트
            if (selectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedPosition)
            }

            // 새로운 아이템 선택
            selectedPosition = currentPosition
            notifyItemChanged(selectedPosition)

            onCategorySelected(categories[currentPosition])
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

    override fun getItemCount() = categories.size

    private fun getIconResource(tMapFacilityUiType: TMapFacilityUiType): Int {
        return when (tMapFacilityUiType) {
            TMapFacilityUiType.SHELTER -> R.drawable.ic_shelter
            TMapFacilityUiType.HOSPITAL -> R.drawable.ic_hospital
            TMapFacilityUiType.GROCERY -> R.drawable.ic_grocery
            else -> 0
        }
    }
}