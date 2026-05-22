package com.example.studyflow.data.model

data class FocusSession(
    val id: String = "",
    val userId: String = "",
    val durationSeconds: Int = 25 * 60,
    val elapsedSeconds: Int = 0,
    val completed: Boolean = false,
    val interrupted: Boolean = false,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long = System.currentTimeMillis()
)
