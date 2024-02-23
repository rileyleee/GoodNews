package com.saveurlife.goodnews.ble.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.ble.BleMeshAdvertiseData
import com.saveurlife.goodnews.ble.service.BleService
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.ItemAroundAdvertiseListBinding
import com.saveurlife.goodnews.map.MiniMapDialogFragment

class BleAdvertiseAdapter (private var userList: List<BleMeshAdvertiseData>, private val sharedViewModel: SharedViewModel, private val bleService: BleService) : RecyclerView.Adapter<BleAdvertiseAdapter.Holder>() {

    // 클릭 리스너 인터페이스 정의
    interface OnItemClickListener {
        fun onButtonClick(deviceId: String)
    }


    var onItemClickListener: OnItemClickListener? = null

    //RecyclerView의 각 항목에 대한 뷰 홀더 객체를 생성
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

        // 연결되지 않은 사용자의 위치 보기 클릭 리스너 (미니맵 띄워줌)
        holder.binding.requestMinimapButton.setOnClickListener {
            // 임시 미니맵
            val miniMapFragment = MiniMapDialogFragment()
            val otherUserLocation = Bundle()
            otherUserLocation.putDouble("latitude", 36.321655)
            otherUserLocation.putDouble("longitude", 127.378953)

            miniMapFragment.arguments = otherUserLocation

            // 다이얼로그를 보여주는 코드 필요 (manager 추가)
            // miniMapFragment.show(childFragmentManager, "MiniMapDialogFragment")
        }

        // requestBleButton 클릭 리스너
        // 광고, 스캔하여 이사람과 연결되고 싶을 때 클릭
        holder.binding.requestBleButton.setOnClickListener {
            sharedViewModel.updateBleDeviceState(user.deviceId, true)
            bleService?.connectOrDisconnect(user.deviceId)
        }
        // stopBleButton 클릭 리스너
        //연결된 사람과 연결을 해제하고 싶을 때 클릭
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

    fun updateDevices(newDevices: List<BleMeshAdvertiseData>) {
        userList = newDevices
        notifyDataSetChanged() // 데이터가 변경되었음을 알림
    }
}