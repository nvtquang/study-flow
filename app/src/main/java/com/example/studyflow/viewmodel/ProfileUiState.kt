package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null
)
