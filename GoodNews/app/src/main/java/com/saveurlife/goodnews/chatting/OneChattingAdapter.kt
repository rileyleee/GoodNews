package com.saveurlife.goodnews.chatting

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.GONE
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.databinding.ItemChattingBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class OneChattingAdapter(private val chattingList: List<OnechattingData>, private val connectedUsers: List<BleMeshConnectedUser>) : RecyclerView.Adapter<OneChattingAdapter.ViewHolder>() {

    // 클릭 리스너 인터페이스 정의
    interface OnItemClickListener {
        fun onItemClick(chatData: OnechattingData)
    }

    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        var binding = ItemChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var chatting = chattingList[position]
        holder.bind(chatting, connectedUsers)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(chatting)
        }
    }

    override fun getItemCount() = chattingList.size

    class ViewHolder(private val binding: ItemChattingBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatting: OnechattingData, connectedUsers: List<BleMeshConnectedUser>){
            binding.chattingName.text = chatting.otherName
            binding.chattingLast.text = chatting.lastChatting

            val formattedDate = formatChattingDate(chatting.date)
            updateStatus(chatting.otherStatus)
            binding.chattingTime.text = formattedDate
            updateReadChatting(chatting.isRead)

            // 상대방이 연결되어 있는지 여부 확인
            val otherUserID = chatting.chatRoomId
            val isUserConnected = connectedUsers.any{it.userId==otherUserID}

            if(isUserConnected){
                binding.root.setBackgroundColor(Color.WHITE)
            }else{
                binding.root.setBackgroundColor(Color.DKGRAY)
            }
        }

        private fun updateStatus(otherStatus: String) {
            when (otherStatus) {
                "safe" -> binding.chattingStatus.setBackgroundResource(R.drawable.my_status_safe_circle)
                "injury" -> binding.chattingStatus.setBackgroundResource(R.drawable.my_status_injury_circle)
                "death" -> binding.chattingStatus.setBackgroundResource(R.drawable.my_status_death_circle)
                "unknown" -> binding.chattingStatus.setBackgroundResource(R.drawable.my_status_circle)
            }
        }

        fun formatChattingDate(chattingDate: String): String {
            // chatting.date 값에서 각 부분을 추출
            val year = chattingDate.substring(0, 2).toInt() + 2000 // "23" -> 2023
            val month = chattingDate.substring(2, 4).toInt() // "11" -> 11
            val day = chattingDate.substring(4, 6).toInt() // "16" -> 16
            val hour = chattingDate.substring(6, 8).toInt() // "11" -> 11
            val minute = chattingDate.substring(8, 10).toInt() // "21" -> 21

            // LocalDateTime 객체 생성
            val dateTime = LocalDateTime.of(year, month, day, hour, minute)
            val now = LocalDateTime.now()

            return if (dateTime.year == now.year && dateTime.month == now.month && dateTime.dayOfMonth == now.dayOfMonth) {
                // 오늘 날짜와 같은 경우 "오전/오후 시:분" 형식으로 포맷팅
                val formatter = DateTimeFormatter.ofPattern("a K:mm", Locale.getDefault())
                dateTime.format(formatter)
            } else {
                // 다른 경우 "월 일" 형식으로 포맷팅
                val formatter = DateTimeFormatter.ofPattern("MM월 dd일", Locale.getDefault())
                dateTime.format(formatter)
            }
        }

        private fun updateReadChatting(isRead: Boolean) {
            when(isRead){
                true -> binding.chattingNew.visibility = GONE
                else -> binding.chattingNew.visibility = View.VISIBLE
            }
        }
    }
}