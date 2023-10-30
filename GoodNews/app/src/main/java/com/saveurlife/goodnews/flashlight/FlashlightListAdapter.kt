package com.saveurlife.goodnews.flashlight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class FlashlightListAdapter(private var list: ArrayList<FlashlightData>) :
    RecyclerView.Adapter<FlashlightListAdapter.ListAdapter>() {

    class ListAdapter(val layout: View) : RecyclerView.ViewHolder(layout) {
        val flashLightTextBox: TextView = layout.findViewById(R.id.flashLightTextBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flash, parent, false)
        return ListAdapter(view)
    }

    override fun onBindViewHolder(holder: ListAdapter, position: Int) {
        holder.flashLightTextBox.text = list[position].content
    }

    override fun getItemCount(): Int {
        return list.size
    }

}