package com.saveurlife.goodnews.chatting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.databinding.ItemChattingBinding
import com.saveurlife.goodnews.databinding.ItemDetailChattingBinding
import com.saveurlife.goodnews.databinding.ItemGroupChattingBinding

class GroupChattingAdapter(private val chattingList: List<OnechattingData>) : RecyclerView.Adapter<GroupChattingAdapter.ViewHolder>(){
    // 클릭 리스너 인터페이스 정의
    interface OnItemClickListener {
        fun onItemClick(chatData: OnechattingData)
    }

    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = ItemGroupChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = chattingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var chatting = chattingList[position]
        holder.bind(chatting)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(chatting)
        }
    }

    class ViewHolder(private val binding: ItemGroupChattingBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatting: OnechattingData){
            binding.groupName.text = chatting.otherName
            updateReadChatting(chatting.isRead)
        }

        private fun updateReadChatting(isRead: Boolean) {
//            when(isRead){
//                true -> binding.chattingNew.visibility = RecyclerView.GONE
//                else -> binding.chattingNew.visibility = View.VISIBLE
//            }
        }
    }


}