package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.GroupRepository
import com.example.studyflow.data.repository.UserRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val groupRepository: GroupRepository = GroupRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    private var registration: ListenerRegistration? = null

    init {
        listenGroups()
    }

    fun listenGroups() {
        registration?.remove()
        val userId = authRepository.currentUserId()
        if (userId.isNullOrBlank()) {
            _uiState.value = GroupsUiState(
                isLoading = false,
                errorMessage = "Bạn cần đăng nhập để xem nhóm học tập."
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        registration = groupRepository.listenUserGroups(
            userId = userId,
            onResult = { groups ->
                _uiState.value = _uiState.value.copy(
                    groups = groups,
                    isLoading = false,
                    errorMessage = null
                )
            },
            onError = { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Không thể tải nhóm học tập."
                )
            }
        )
    }

    fun updateQuery(value: String) {
        _uiState.value = _uiState.value.copy(query = value)
    }

    fun createGroup(name: String, description: String) {
        if (name.trim().isBlank()) {
            _uiState.value = _uiState.value.copy(message = "Vui lòng nhập tên nhóm.")
            return
        }

        _uiState.value = _uiState.value.copy(isCreating = true, message = null)
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để tạo nhóm.")
                val profile = userRepository.getUserProfile(userId)
                groupRepository.createGroup(
                    userId = userId,
                    userName = profile?.displayName.orEmpty(),
                    name = name.trim(),
                    description = description.trim()
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    message = "Đã tạo nhóm mới."
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    message = throwable.message ?: "Không thể tạo nhóm."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    override fun onCleared() {
        registration?.remove()
        super.onCleared()
    }
}
