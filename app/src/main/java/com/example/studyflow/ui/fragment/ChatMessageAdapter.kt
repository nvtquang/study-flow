package com.example.studyflow.ui.fragment

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.R
import com.example.studyflow.data.model.ChatMessage
import com.example.studyflow.databinding.ItemChatMessageBinding

class ChatMessageAdapter : RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {
    private val messages = mutableListOf<ChatMessage>()
    private var currentUserId: String = ""

    fun submitList(nextMessages: List<ChatMessage>, userId: String) {
        currentUserId = userId
        messages.clear()
        messages.addAll(nextMessages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position], messages[position].senderId == currentUserId)
    }

    override fun getItemCount(): Int = messages.size

    class ViewHolder(
        private val binding: ItemChatMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage, isMine: Boolean) {
            val params = binding.messageContainer.layoutParams as FrameLayout.LayoutParams
            params.gravity = if (isMine) Gravity.END else Gravity.START
            binding.messageContainer.layoutParams = params
            binding.messageContainer.setBackgroundResource(
                if (isMine) R.drawable.bg_chat_mine else R.drawable.bg_chat_other
            )
            binding.senderTextView.text = if (isMine) "Bạn" else message.senderName.ifBlank { "Thành viên" }
            binding.senderTextView.isVisible = !isMine
            binding.messageTextView.text = message.text
            binding.senderTextView.setTextColor(if (isMine) Color.WHITE else Color.rgb(59, 130, 246))
            binding.messageTextView.setTextColor(if (isMine) Color.WHITE else Color.rgb(17, 24, 39))
        }
    }
}
