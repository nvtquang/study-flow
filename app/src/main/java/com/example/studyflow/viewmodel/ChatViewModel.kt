package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.ChatMessage
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.ChatRepository
import com.example.studyflow.data.repository.GroupRepository
import com.example.studyflow.data.repository.UserRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val groupRepository: GroupRepository = GroupRepository(),
    private val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var messageRegistration: ListenerRegistration? = null
    private var activeGroupId: String? = null

    fun start(groupId: String) {
        if (activeGroupId == groupId) return

        activeGroupId = groupId
        messageRegistration?.remove()
        _uiState.value = ChatUiState(isLoading = true)

        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Ban can dang nhap de chat.")
                val profile = userRepository.getUserProfile(userId)
                val group = groupRepository.getGroup(groupId)
                    ?: error("Khong tim thay nhom.")
                if (userId !in group.memberIds) {
                    error("Ban can tham gia nhom truoc khi chat.")
                }
                Triple(userId, profile?.displayName.orEmpty().ifBlank { "Ban" }, group)
            }.onSuccess { (userId, userName, group) ->
                _uiState.value = _uiState.value.copy(
                    currentUserId = userId,
                    currentUserName = userName,
                    group = group,
                    isCurrentUserMember = true,
                    isLoading = false,
                    errorMessage = null
                )
                listenMessages(groupId)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Khong the mo cuoc tro chuyen."
                )
            }
        }
    }

    fun updateInput(value: String) {
        _uiState.value = _uiState.value.copy(input = value)
    }

    fun sendMessage() {
        val current = _uiState.value
        val text = current.input.trim()
        val groupId = current.group?.id.orEmpty()
        if (text.isBlank() || groupId.isBlank() || !current.isCurrentUserMember) return

        _uiState.value = current.copy(input = "")
        viewModelScope.launch {
            runCatching {
                chatRepository.sendMessage(
                    ChatMessage(
                        groupId = groupId,
                        senderId = current.currentUserId,
                        senderName = current.currentUserName,
                        text = text
                    )
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    message = throwable.message ?: "Khong the gui tin nhan."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    private fun listenMessages(groupId: String) {
        messageRegistration = chatRepository.listenMessages(
            groupId = groupId,
            onResult = { messages ->
                _uiState.value = _uiState.value.copy(messages = messages, isLoading = false)
            },
            onError = { throwable ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.message ?: "Khong the tai tin nhan.",
                    isLoading = false
                )
            }
        )
    }

    override fun onCleared() {
        messageRegistration?.remove()
        super.onCleared()
    }
}
