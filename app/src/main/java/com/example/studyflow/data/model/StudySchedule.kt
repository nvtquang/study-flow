package com.example.studyflow.data.model

data class StudySchedule(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val eventType: String = "Bài giảng",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
