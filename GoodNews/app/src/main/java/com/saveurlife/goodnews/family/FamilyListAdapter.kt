package com.saveurlife.goodnews.family

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R

class FamilyListAdapter(private val familyList: List<FamilyData>) :
    RecyclerView.Adapter<FamilyListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.familyNameTextView)
        val statusView: View = view.findViewById(R.id.familyStatusCircle)
        val lastAccessTimeView: TextView = view.findViewById(R.id.familyLastAccessTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_family, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = familyList[position]
        holder.nameView.text = item.name
        holder.lastAccessTimeView.text = item.lastAccessTime

        when (item.status) {
            Status.HEALTHY -> {
                holder.statusView.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.safe)
            }

            Status.INJURED -> {
                holder.statusView.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.caution)
            }

            Status.DECEASED -> {
                holder.statusView.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.black)
            }

            else -> {
                holder.statusView.backgroundTintList = null
            }
        }
    }

    override fun getItemCount(): Int = familyList.size
}