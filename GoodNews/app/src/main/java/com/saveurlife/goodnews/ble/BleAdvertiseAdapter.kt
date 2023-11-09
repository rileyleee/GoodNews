package com.saveurlife.goodnews.ble

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.databinding.ItemAroundAdvertiseListBinding

class BleAdvertiseAdapter (private val userList: List<BleMeshAdvertiseData>) : RecyclerView.Adapter<BleAdvertiseAdapter.Holder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding = ItemAroundAdvertiseListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val user = userList[position]
        holder.bind(user)

        if (user.isRequestingBle) {
            holder.binding.requestBleButton.visibility = View.GONE
            holder.binding.cutBleButton.visibility = View.VISIBLE
        } else {
            holder.binding.requestBleButton.visibility = View.VISIBLE
            holder.binding.cutBleButton.visibility = View.GONE
        }

        // requestBleButton 클릭 리스너
        holder.binding.requestBleButton.setOnClickListener {
            user.isRequestingBle = true
            notifyItemChanged(position)
        }

        // stopBleButton 클릭 리스너
        holder.binding.cutBleButton.setOnClickListener {
            user.isRequestingBle = false
            notifyItemChanged(position)
        }
    }

    class Holder(val binding: ItemAroundAdvertiseListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(user: BleMeshAdvertiseData) {
            binding.advertiseName.text = user.name
        }
    }

}