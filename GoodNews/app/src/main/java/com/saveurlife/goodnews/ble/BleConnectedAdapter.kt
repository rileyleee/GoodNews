package com.saveurlife.goodnews.ble

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ItemAroundListBinding

class BleConnectedAdapter(private val userList: List<BleMeshConnectedUser>) : RecyclerView.Adapter<BleConnectedAdapter.UserViewHolder>() {

    //뷰 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAroundListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    //데이터 바인딩
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    //리스트의 크기만큼 반환
    override fun getItemCount() = userList.size

    class UserViewHolder(private val binding: ItemAroundListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: BleMeshConnectedUser) {
            // itemView에서 뷰 요소에 접근하여 데이터 설정
            binding.aroundName.text = user.userName
            updateHealthStatusBackground(user.healthStatus)
        }

        //상태(숫자)에 따라 색 변경
        private fun updateHealthStatusBackground(
            healthStatus: String
        ) {
            when (healthStatus) {
                "1" ->  this.binding.aroundStatus.setBackgroundResource(R.drawable.my_status_safe_circle)
                "2" ->  this.binding.aroundStatus.setBackgroundResource(R.drawable.my_status_injury_circle)
                "3" ->  this.binding.aroundStatus.setBackgroundResource(R.drawable.my_status_death_circle)
                "4" ->  this.binding.aroundStatus.setBackgroundResource(R.drawable.my_status_circle)
                else ->  this.binding.aroundStatus.setBackgroundResource(R.drawable.my_status_circle)
            }
        }
    }
}
