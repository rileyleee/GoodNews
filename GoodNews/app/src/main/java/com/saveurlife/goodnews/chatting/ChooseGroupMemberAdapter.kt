//package com.saveurlife.goodnews.chatting
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.saveurlife.goodnews.R
//import com.saveurlife.goodnews.ble.BleMeshConnectedUser
//import com.saveurlife.goodnews.databinding.ItemChooseGroupBinding
//import com.saveurlife.goodnews.models.ChatMessage
//
//class ChooseGroupMemberAdapter(private var userList: List<BleMeshConnectedUser>): RecyclerView.Adapter<ChooseGroupMemberAdapter.ViewHolder>() {
//
//    // 선택된 사용자 ID를 저장하는 리스트
//    private val selectedUserIds = mutableListOf<String>()
//
//
//    fun getSelectedUserIds(): List<String> {
//        return selectedUserIds
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemChooseGroupBinding.inflate(inflater, parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val user = userList[position]
//        holder.bind(user)
//
//        holder.binding.radio.setOnClickListener {
//            // 현재 모델의 선택 상태를 반전시킴
//            val isChecked = holder.binding.radio.isChecked
//            user.isSelected = isChecked
//
//            if (isChecked) {
//                selectedUserIds.add(user.userId) // 사용자 ID 추가
//                println("저장되는 값 ${user.userId}")
//                println("리스트 $selectedUserIds")
//                println("리스트 사이즈 ${selectedUserIds.size}")
//            } else {
//                selectedUserIds.remove(user.userId) // 사용자 ID 제거
//                println("리스트 사라지는 사이즈 ${selectedUserIds.size}")
//            }
//            // UI에 바로 상태 반영
//            holder.binding.radio.isChecked = user.isSelected
//        }
//    }
//
//    override fun getItemCount() = userList.size
//
//    class ViewHolder(val binding: ItemChooseGroupBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(user: BleMeshConnectedUser) {
//            binding.aroundNameChoose.text = user.userName
//            binding.radio.isChecked = user.isSelected
//            println("${user.userName} 뭘 받아와?")
//            updateHealthStatusBackground(user.healthStatus)
//        }
//
//        private fun updateHealthStatusBackground(healthStatus: String) {
//            when (healthStatus) {
//                "safe" ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_safe_circle)
//                "injury" ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_injury_circle)
//                "death" ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_death_circle)
//                "unknown" ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_circle)
//                else ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_circle)
//            }
//        }
//
//    }
//    fun updateUsers(newUsers: List<BleMeshConnectedUser>) {
//        userList = newUsers
//        notifyDataSetChanged()
//    }
//}