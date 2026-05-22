package com.example.studyflow.data.model

data class AiMessage(
    val id: String = System.currentTimeMillis().toString(),
    val text: String = "",
    val fromUser: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
