package com.example.studyflow.data.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val currentStreak: Int = 0,
    val totalFocusSeconds: Long = 0L,
    val focusScore: Int = 0
)
