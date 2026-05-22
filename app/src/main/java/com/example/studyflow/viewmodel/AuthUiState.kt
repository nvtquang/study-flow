package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.UserProfile

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: UserProfile? = null,
    val errorMessage: String? = null
)
