package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.AiMessage

data class AiAssistantUiState(
    val messages: List<AiMessage> = listOf(
        AiMessage(
            text = "Chào bạn, mình là StudyFlow AI. Bạn có thể hỏi mình để tóm tắt, giải thích, tạo quiz hoặc rút ý chính từ tài liệu.",
            fromUser = false
        )
    ),
    val input: String = "",
    val isTyping: Boolean = false,
    val errorMessage: String? = null
)
