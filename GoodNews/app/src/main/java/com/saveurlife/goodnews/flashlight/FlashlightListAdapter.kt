package com.saveurlife.goodnews.flashlight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class FlashlightListAdapter(var list: ArrayList<String>) :
    RecyclerView.Adapter<FlashlightListAdapter.ListAdapter>() {

    class ListAdapter(val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter {
        return ListAdapter(
            LayoutInflater.from(parent.context).inflate(R.layout.item_flash, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListAdapter, position: Int) {
        holder.layout.flashLightTextBox.text = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

}