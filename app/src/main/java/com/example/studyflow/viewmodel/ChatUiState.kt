package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.ChatMessage
import com.example.studyflow.data.model.StudyGroup

data class ChatUiState(
    val group: StudyGroup? = null,
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val currentUserId: String = "",
    val currentUserName: String = "Ban",
    val isCurrentUserMember: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val message: String? = null
)
