package com.saveurlife.goodnews.chatting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.databinding.ItemChattingBinding
import com.saveurlife.goodnews.databinding.ItemDetailChattingBinding

class ChattingDetailAdapter(private val chatting: List<ChattingDetailData>) : RecyclerView.Adapter<ChattingDetailAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChattingDetailAdapter.ViewHolder {
        var binding = ItemDetailChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChattingDetailAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChattingDetailAdapter.ViewHolder, position: Int) {
        var chatting = chatting[position]
        holder.bind(chatting)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ViewHolder(private val binding: ItemDetailChattingBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatting: ChattingDetailData){
            binding.chatDetailName.text = chatting.userId
            binding.chatDetailContext.text = chatting.message
        }


    }


}