package com.example.studyflow.ui.fragment

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.R
import com.example.studyflow.data.model.AiMessage
import com.example.studyflow.databinding.ItemAiMessageBinding

class AiMessageAdapter : RecyclerView.Adapter<AiMessageAdapter.ViewHolder>() {
    private val messages = mutableListOf<AiMessage>()

    fun submitList(nextMessages: List<AiMessage>) {
        messages.clear()
        messages.addAll(nextMessages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAiMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    class ViewHolder(
        private val binding: ItemAiMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: AiMessage) {
            val isMine = message.fromUser
            val params = binding.messageContainer.layoutParams as FrameLayout.LayoutParams
            params.gravity = if (isMine) Gravity.END else Gravity.START
            binding.messageContainer.layoutParams = params
            binding.messageContainer.setBackgroundResource(
                if (isMine) R.drawable.bg_ai_user else R.drawable.bg_ai_assistant
            )
            binding.roleTextView.text = if (isMine) "Bạn" else "StudyFlow AI"
            binding.messageTextView.text = message.text
            binding.roleTextView.setTextColor(if (isMine) Color.WHITE else Color.rgb(59, 130, 246))
            binding.messageTextView.setTextColor(if (isMine) Color.WHITE else Color.rgb(17, 24, 39))
        }
    }
}
