package com.example.studyflow.data.model

data class NotificationSettings(
    val userId: String = "",
    val classReminders: Boolean = true,
    val deadlineAlerts: Boolean = true,
    val focusReminders: Boolean = true,
    val groupChatNotifications: Boolean = true,
    val dailySummary: Boolean = false,
    val summaryTime: String = "20:00"
)
