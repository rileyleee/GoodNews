package com.saveurlife.goodnews.chatting

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ItemChattingBinding
import com.saveurlife.goodnews.databinding.ItemDetailChattingBinding
import org.apache.commons.lang3.mutable.Mutable

class ChattingDetailAdapter(private val chatting: MutableList<ChattingDetailData>) : RecyclerView.Adapter<ChattingDetailAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        var binding = ItemDetailChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var chatting = chatting[position]
        holder.bind(chatting)
    }

    override fun getItemCount() = chatting.size
    fun addMessage(newMessage: ChattingDetailData) {
        chatting.add(newMessage)
        notifyItemInserted(chatting.size - 1)
    }

    class ViewHolder(private val binding: ItemDetailChattingBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatting: ChattingDetailData){
            binding.chatDetailName.text = chatting.userId
            binding.chatDetailContext.text = chatting.message
            binding.chatDetailTime.text = chatting.time

            val dpToPx = { dp: Int ->
                (dp * Resources.getSystem().displayMetrics.density).toInt()
            }

            val layoutParams = binding.chatDetailContext.layoutParams as ConstraintLayout.LayoutParams
            val timeLayoutParams = binding.chatDetailTime.layoutParams as ConstraintLayout.LayoutParams

            if (chatting.isUserMessage) {
                // 사용자 메시지의 경우, TextView의 배경색 변경
                binding.chatDetailContext.backgroundTintList =
                    ContextCompat.getColorStateList(binding.chatDetailContext.context, R.color.chatting)
                layoutParams.endToEnd = R.id.chatDetailGuideEnd
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                binding.chatDetailContext.layoutParams = layoutParams

                timeLayoutParams.startToEnd = ConstraintLayout.LayoutParams.UNSET
                timeLayoutParams.endToStart = R.id.chatDetailContext
                timeLayoutParams.marginEnd = dpToPx(5)

                binding.chatDetailName.visibility = View.GONE


            } else {
                // 다른 사람 메시지의 경우, 다른 배경색 설정
                binding.chatDetailContext.backgroundTintList =
                    ContextCompat.getColorStateList(binding.chatDetailContext.context, R.color.white)
                layoutParams.startToStart = R.id.chatDetailGuide
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
                binding.chatDetailContext.layoutParams = layoutParams

                timeLayoutParams.startToEnd = R.id.chatDetailContext

                binding.chatDetailName.visibility = View.VISIBLE
            }
        }
    }
}