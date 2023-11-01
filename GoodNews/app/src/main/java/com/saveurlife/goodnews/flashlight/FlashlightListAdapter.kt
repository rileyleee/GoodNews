package com.saveurlife.goodnews.flashlight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class FlashlightListAdapter(private var list: ArrayList<FlashlightData>) :
    RecyclerView.Adapter<FlashlightListAdapter.ListAdapter>() {

    // isFlashing boolean
    var isFlashing = false

    // 클릭 리스너
    interface OnItemClickListener {
        fun onItemClick(data: FlashlightData, position: Int)
    }

    // 리스너 객체 참조 저장
    private var listener: OnItemClickListener? = null

    // 리스너 객체 참조 => 어댑터에 전달
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ListAdapter(val layout: View) : RecyclerView.ViewHolder(layout) {
        val flashLightTextBox: TextView = layout.findViewById(R.id.flashLightTextBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flash, parent, false)
        return ListAdapter(view)
    }

    override fun onBindViewHolder(holder: ListAdapter, position: Int) {
        val data = list[position]
        holder.flashLightTextBox.text = list[position].content

        // 아이템 클릭 시 리스너 onItemClick 메서드 호출
        holder.itemView.setOnClickListener {
            listener?.onItemClick(data, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}