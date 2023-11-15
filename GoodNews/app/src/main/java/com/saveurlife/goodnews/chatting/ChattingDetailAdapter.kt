package com.saveurlife.goodnews.chatting

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ItemDetailChattingBinding
import com.saveurlife.goodnews.models.ChatMessage
import io.realm.kotlin.ext.isValid
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChattingDetailAdapter(private var messages: List<ChatMessage>) : RecyclerView.Adapter<ChattingDetailAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDetailChattingBinding.inflate(inflater, parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(private val binding: ItemDetailChattingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) {
            binding.chatDetailName.text = chatMessage.senderName
            binding.chatDetailContext.text = chatMessage.content

            val currentFormatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS")
            // 새로운 포맷
            val newFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)

            try {
                val parsedDateTime = LocalDateTime.parse(chatMessage.time, currentFormatter)
                val formattedTime = parsedDateTime.format(newFormatter)
                binding.chatDetailTime.text = formattedTime
            } catch (e: Exception) {
                binding.chatDetailTime.text = "오류"
            }

            val dpToPx = { dp: Int ->
                (dp * Resources.getSystem().displayMetrics.density).toInt()
            }
            val layoutParams = binding.chatDetailContext.layoutParams as ConstraintLayout.LayoutParams
            val timeLayoutParams = binding.chatDetailTime.layoutParams as ConstraintLayout.LayoutParams

            if (chatMessage.senderId != chatMessage.chatRoomId) {
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
            }else {
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

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}

