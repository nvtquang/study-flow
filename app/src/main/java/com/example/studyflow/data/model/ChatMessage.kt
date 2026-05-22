package com.example.studyflow.data.model

data class ChatMessage(
    val id: String = "",
    val groupId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
