package com.saveurlife.goodnews.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.sync.TimeService

class EmergencyListAdapter(private val closeInfoList: List<MapInstantInfo>) : RecyclerView.Adapter<EmergencyListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_emergency_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val closeInfo = closeInfoList[position]
        holder.bind(closeInfo)
    }

    override fun getItemCount(): Int {
        return closeInfoList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emergencyInfoContent: TextView = itemView.findViewById(R.id.emergencyInfoContent)
        private val emergencyLastUpdateTime: TextView = itemView.findViewById(R.id.emergencyLastUpdateTime)
        val timeService = TimeService()

        fun bind(closeInfo: MapInstantInfo) {
            // MapInstantInfo에서 데이터를 가져와 각 뷰에 설정합니다.
            emergencyInfoContent.text = closeInfo.content
            emergencyLastUpdateTime.text = timeService.realmInstantToString(closeInfo.time)
        }
    }
}
