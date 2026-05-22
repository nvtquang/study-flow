package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.UserProfile
import com.example.studyflow.data.repository.UserRepository
import com.example.studyflow.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    private val _profileState = MutableStateFlow<UiState<UserProfile>>(UiState.Idle)
    val profileState: StateFlow<UiState<UserProfile>> = _profileState.asStateFlow()

    fun loadProfile(uid: String) {
        _profileState.value = UiState.Loading
        viewModelScope.launch {
            runCatching {
                userRepository.getUserProfile(uid)
            }.onSuccess { profile ->
                _profileState.value = if (profile == null) {
                    UiState.Empty
                } else {
                    UiState.Success(profile)
                }
            }.onFailure { throwable ->
                _profileState.value = UiState.Error(throwable.message ?: "Unable to load profile.")
            }
        }
    }

    fun saveProfile(profile: UserProfile) {
        _profileState.value = UiState.Loading
        viewModelScope.launch {
            runCatching {
                userRepository.saveUserProfile(profile)
                profile
            }.onSuccess { savedProfile ->
                _profileState.value = UiState.Success(savedProfile)
            }.onFailure { throwable ->
                _profileState.value = UiState.Error(throwable.message ?: "Unable to save profile.")
            }
        }
    }
}
