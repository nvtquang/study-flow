package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.AiMessage
import com.example.studyflow.data.repository.AiRepository
import com.example.studyflow.data.repository.MockAiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiAssistantViewModel(
    private val aiRepository: AiRepository = MockAiRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    fun updateInput(value: String) {
        _uiState.value = _uiState.value.copy(input = value, errorMessage = null)
    }

    fun sendMessage() {
        val question = _uiState.value.input.trim()
        if (question.isBlank() || _uiState.value.isTyping) return

        val userMessage = AiMessage(text = question, fromUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            input = "",
            isTyping = true,
            errorMessage = null
        )

        viewModelScope.launch {
            runCatching {
                aiRepository.ask(question)
            }.onSuccess { answer ->
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + AiMessage(text = answer, fromUser = false),
                    isTyping = false
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isTyping = false,
                    errorMessage = throwable.message ?: "Không thể nhận phản hồi AI."
                )
            }
        }
    }
}
