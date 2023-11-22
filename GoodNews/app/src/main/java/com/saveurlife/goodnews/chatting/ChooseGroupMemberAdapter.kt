package com.saveurlife.goodnews.chatting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.databinding.ItemChooseGroupBinding

class ChooseGroupMemberAdapter(private var userList: List<BleMeshConnectedUser>): RecyclerView.Adapter<ChooseGroupMemberAdapter.ViewHolder>() {

    // 선택된 사용자 ID를 저장하는 리스트
    private val selectedUserIds = mutableMapOf<String, Boolean>()

    // selectedUserIds 맵을 반환하는 public 메서드
    fun getSelectedUserIds(): Map<String, Boolean> {
        return selectedUserIds
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChooseGroupBinding.inflate(inflater, parent, false)
        val viewHolder = ViewHolder(binding, this)

        binding.radio.setOnCheckedChangeListener { _, isChecked ->
            val user = userList[viewHolder.adapterPosition+1]
            if (isChecked) {
                selectedUserIds[user.userId] = true
            } else {
                selectedUserIds.remove(user.userId)
            }
            println("selectedUserIds: $selectedUserIds")
        }
        return ViewHolder(binding, this)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
//        holder.binding.radio.setOnCheckedChangeListener(null)
        holder.bind(user, selectedUserIds[user.userId] == true)
//        holder.binding.radio.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                selectedUserIds[user.userId] = true
//                println("selectedUserIds: $selectedUserIds")
//            } else {
//                selectedUserIds.remove(user.userId)
//                println("selectedUserIds-f: $selectedUserIds")
//            }
//        }
    }

//    override fun getItemViewType(position: Int): Int {
//        return position
//    }

    override fun getItemCount() = userList.size

    class ViewHolder(val binding: ItemChooseGroupBinding, val adapter: ChooseGroupMemberAdapter) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: BleMeshConnectedUser, isChecked: Boolean) {
            binding.aroundNameChoose.text = user.userName
            binding.radio.isChecked = isChecked
            updateHealthStatusBackground(user.healthStatus)
        }

        private fun updateHealthStatusBackground(healthStatus: String) {
            when (healthStatus) {
                "safe" ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_safe_circle)
                "injury" ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_injury_circle)
                "death" ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_death_circle)
                "unknown" ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_circle)
                else ->  this.binding.aroundStatusChoose.setBackgroundResource(R.drawable.my_status_circle)
            }
        }

    }
    fun updateUsers(newUsers: List<BleMeshConnectedUser>) {
        userList = newUsers
        notifyDataSetChanged()
    }
}