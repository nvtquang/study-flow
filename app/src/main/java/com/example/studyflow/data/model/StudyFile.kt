package com.example.studyflow.data.model

data class StudyFile(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val storagePath: String = "",
    val downloadUrl: String = "",
    val contentType: String = "",
    val sizeBytes: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
