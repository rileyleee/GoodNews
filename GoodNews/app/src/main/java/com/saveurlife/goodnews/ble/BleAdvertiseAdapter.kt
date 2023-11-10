package com.saveurlife.goodnews.ble

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.ItemAroundAdvertiseListBinding

class BleAdvertiseAdapter (private var userList: List<BleMeshAdvertiseData>, private val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<BleAdvertiseAdapter.Holder>() {

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

        // requestBleButton 클릭 리스너
        holder.binding.requestBleButton.setOnClickListener {
            sharedViewModel.updateBleDeviceState(user.deviceId, true)
        }
        // stopBleButton 클릭 리스너
        holder.binding.cutBleButton.setOnClickListener {
            sharedViewModel.updateBleDeviceState(user.deviceId, false)
        }

        if (user.isRequestingBle) {
            holder.binding.requestBleButton.visibility = View.GONE
            holder.binding.cutBleButton.visibility = View.VISIBLE
        } else {
            holder.binding.requestBleButton.visibility = View.VISIBLE
            holder.binding.cutBleButton.visibility = View.GONE
        }
    }

    class Holder(val binding: ItemAroundAdvertiseListBinding) : RecyclerView.ViewHolder(binding.root){
        //bind 함수는 데이터를 뷰에 바인딩하는 역할에 집중하는 것이 좋음
        fun bind(user: BleMeshAdvertiseData) {
            binding.advertiseName.text = user.name
        }
    }
}