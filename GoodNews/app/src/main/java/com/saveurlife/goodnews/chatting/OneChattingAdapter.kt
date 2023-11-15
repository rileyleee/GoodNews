package com.saveurlife.goodnews.chatting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.GONE
import com.saveurlife.goodnews.databinding.ItemChattingBinding

class OneChattingAdapter(private val chattingList: List<OnechattingData>) : RecyclerView.Adapter<OneChattingAdapter.ViewHolder>() {

    // 클릭 리스너 인터페이스 정의
    interface OnItemClickListener {
        fun onItemClick(chatData: OnechattingData)
    }

//    interface OnChattingItemClickListener {
//        fun onItemClicked(data: OnechattingData)
//    }

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
        holder.bind(chatting)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(chatting)
        }
    }

    override fun getItemCount() = chattingList.size

    class ViewHolder(private val binding: ItemChattingBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatting: OnechattingData){
            binding.chattingName.text = chatting.otherName
            binding.chattingLast.text = chatting.lastChatting
            binding.chattingTime.text = chatting.date
            updateReadChatting(chatting.isRead)
        }

        private fun updateReadChatting(isRead: Boolean) {
            when(isRead){
                true -> binding.chattingNew.visibility = GONE
                else -> binding.chattingNew.visibility = View.VISIBLE
            }
        }
    }
}