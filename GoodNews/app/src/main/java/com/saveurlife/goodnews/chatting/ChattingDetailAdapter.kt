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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChattingDetailAdapter(private var messages: List<ChatMessage>, private val myUserId: String) : RecyclerView.Adapter<ChattingDetailAdapter.MessageViewHolder>() {

    companion object {
        fun formatTime(time: String): String {
            val currentFormatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS")
            val newFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)
            val parsedDateTime = LocalDateTime.parse(time, currentFormatter)
            return parsedDateTime.format(newFormatter)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDetailChattingBinding.inflate(inflater, parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val nextMessage = if (position < messages.size - 1) messages[position + 1] else null

        val showTime = shouldShowTime(message, nextMessage)
        holder.bind(message, showTime)
    }

    private fun shouldShowTime(currentMessage: ChatMessage, nextMessage: ChatMessage?): Boolean {
        if (nextMessage == null) return true
        val sameSender = currentMessage.senderId == nextMessage.senderId
        val sameTime = formatTime(currentMessage.time) == formatTime(nextMessage.time)

        return !(sameSender && sameTime)
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MessageViewHolder(private val binding: ItemDetailChattingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage, showTime: Boolean) {
            Log.d("ChattingDetailAdapter", "Binding message: ${chatMessage.content}, Time: ${chatMessage.time}")
            binding.chatDetailName.text = chatMessage.senderName
            binding.chatDetailContext.text = chatMessage.content

            binding.chatDetailTime.visibility = if (showTime) View.VISIBLE else View.GONE
            if (showTime) {
                binding.chatDetailTime.text = formatTime(chatMessage.time)
            }

            val dpToPx = { dp: Int ->
                (dp * Resources.getSystem().displayMetrics.density).toInt()
            }
            val layoutParams = binding.chatDetailContext.layoutParams as ConstraintLayout.LayoutParams
            val timeLayoutParams = binding.chatDetailTime.layoutParams as ConstraintLayout.LayoutParams

            if (chatMessage.senderId != chatMessage.chatRoomId) {
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
