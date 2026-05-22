package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            isEmpty = false,
            dashboard = HomeDashboardData.mock()
        )

        viewModelScope.launch {
            runCatching {
                val uid = authRepository.currentUserId()
                if (uid.isNullOrBlank()) {
                    null
                } else {
                    userRepository.getUserProfile(uid)
                }
            }.onSuccess { profile ->
                _uiState.value = HomeUiState(
                    isLoading = false,
                    profile = profile,
                    dashboard = HomeDashboardData.mock(),
                    isEmpty = profile == null
                )
            }.onFailure { throwable ->
                _uiState.value = HomeUiState(
                    isLoading = false,
                    dashboard = HomeDashboardData.mock(),
                    errorMessage = throwable.message ?: "Không thể tải dữ liệu người dùng."
                )
            }
        }
    }
}
