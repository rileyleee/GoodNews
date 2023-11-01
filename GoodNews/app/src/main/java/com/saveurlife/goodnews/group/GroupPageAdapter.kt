package com.saveurlife.goodnews.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class GroupPageAdapter(private val itemList: MutableList<GroupData>) : RecyclerView.Adapter<GroupPageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        // 아이템 내용 설정
    }

    override fun getItemCount() = itemList.size

    // 스와이프로 아이템 삭제
    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }



}