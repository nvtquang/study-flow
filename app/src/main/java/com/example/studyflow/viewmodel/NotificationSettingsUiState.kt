package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.NotificationSettings

data class NotificationSettingsUiState(
    val settings: NotificationSettings = NotificationSettings(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null
)
