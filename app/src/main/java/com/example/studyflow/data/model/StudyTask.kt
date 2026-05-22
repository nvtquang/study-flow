package com.example.studyflow.data.model

data class StudyTask(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val eventType: String = "Hạn chót",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val note: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
