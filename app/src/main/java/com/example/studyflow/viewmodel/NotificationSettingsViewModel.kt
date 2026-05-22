package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.NotificationSettings
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.NotificationSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationSettingsViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val repository: NotificationSettingsRepository = NotificationSettingsRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để xem cài đặt.")
                repository.getSettings(userId)
            }.onSuccess { settings ->
                _uiState.value = NotificationSettingsUiState(settings = settings, isLoading = false)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Không thể tải cài đặt thông báo."
                )
            }
        }
    }

    fun update(transform: (NotificationSettings) -> NotificationSettings) {
        val userId = authRepository.currentUserId()
        if (userId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(message = "Bạn cần đăng nhập để lưu cài đặt.")
            return
        }

        val next = transform(_uiState.value.settings.copy(userId = userId))
        _uiState.value = _uiState.value.copy(settings = next, isSaving = true, message = null)
        viewModelScope.launch {
            runCatching {
                repository.saveSettings(next)
            }.onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, message = "Đã lưu cài đặt.")
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = throwable.message ?: "Không thể lưu cài đặt."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
