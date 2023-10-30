package com.saveurlife.goodnews.flashlight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class FlashlightRecordAdapter(private var list: ArrayList<FlashlightData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 아이템 뷰 타입 상수 => 가독성, 유지 관리성 향상
    companion object {
        const val TYPE_SELF = 1
        const val TYPE_OTHER = 2
    }

    // SELF 아이템 뷰 홀더
    class SelfViewHolder(val layout: View) : RecyclerView.ViewHolder(layout) {
        val flashSelfItem: TextView = layout.findViewById(R.id.flash_self_item)
    }

    // OTHER 아이템 뷰 홀더
    class OtherViewHolder(val layout: View) : RecyclerView.ViewHolder(layout) {
        val flashOtherItem: TextView = layout.findViewById(R.id.flash_other_item)
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].type) {
            FlashType.SELF -> TYPE_SELF
            FlashType.OTHER -> TYPE_OTHER
            else -> throw IllegalArgumentException("Unknown FlashType in getItemViewType")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SELF -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_self_flash, parent, false)
                SelfViewHolder(view)
            }

            TYPE_OTHER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_other_flash, parent, false)
                OtherViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (item.type) {
            FlashType.SELF -> {
                val selfHolder = holder as SelfViewHolder
                selfHolder.flashSelfItem.text = item.content
            }

            FlashType.OTHER -> {
                val otherHolder = holder as OtherViewHolder
                otherHolder.flashOtherItem.text = item.content
            }

            else -> throw IllegalArgumentException("Unknown FlashType in onBindViewHolder")
        }
    }
}