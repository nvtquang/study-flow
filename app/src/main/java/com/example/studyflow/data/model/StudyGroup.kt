package com.example.studyflow.data.model

data class StudyGroup(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val subjectIcon: String = "S",
    val memberIds: List<String> = emptyList(),
    val memberNames: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageAt: Long = 0L,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
