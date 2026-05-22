package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                val uid = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để xem hồ sơ.")
                userRepository.getUserProfile(uid)
            }.onSuccess { profile ->
                _uiState.value = ProfileUiState(
                    profile = profile,
                    isLoading = false,
                    errorMessage = if (profile == null) "Chưa có hồ sơ người dùng." else null
                )
            }.onFailure { throwable ->
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Không thể tải hồ sơ."
                )
            }
        }
    }

    fun updateDisplayName(displayName: String) {
        val profile = _uiState.value.profile ?: return
        if (displayName.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(message = "Tên không được để trống.")
            return
        }

        _uiState.value = _uiState.value.copy(isSaving = true, message = null)
        viewModelScope.launch {
            runCatching {
                val updated = profile.copy(displayName = displayName.trim())
                userRepository.saveUserProfile(updated)
                updated
            }.onSuccess { updated ->
                _uiState.value = _uiState.value.copy(
                    profile = updated,
                    isSaving = false,
                    message = "Đã cập nhật hồ sơ."
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = throwable.message ?: "Không thể cập nhật hồ sơ."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
