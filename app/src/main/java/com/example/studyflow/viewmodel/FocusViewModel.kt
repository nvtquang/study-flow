package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.FocusSession
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.FocusRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FocusViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val focusRepository: FocusRepository = FocusRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun start() {
        if (_uiState.value.status == FocusTimerStatus.Running) return

        _uiState.value = FocusUiState(
            status = FocusTimerStatus.Running,
            startedAt = System.currentTimeMillis()
        )
        startTicker()
    }

    fun pause() {
        if (_uiState.value.status != FocusTimerStatus.Running) return

        timerJob?.cancel()
        timerJob = null
        _uiState.value = _uiState.value.copy(status = FocusTimerStatus.Paused)
    }

    fun resume() {
        if (_uiState.value.status != FocusTimerStatus.Paused) return

        _uiState.value = _uiState.value.copy(status = FocusTimerStatus.Running)
        startTicker()
    }

    fun reset() {
        val current = _uiState.value
        timerJob?.cancel()
        timerJob = null

        if (current.status == FocusTimerStatus.Running || current.status == FocusTimerStatus.Paused) {
            saveInterruptedSession(current)
            _uiState.value = FocusUiState(
                status = FocusTimerStatus.Interrupted,
                message = "Cây của bạn đã héo mất rồi!"
            )
        } else {
            _uiState.value = FocusUiState()
        }
    }

    fun retry() {
        timerJob?.cancel()
        timerJob = null
        _uiState.value = FocusUiState()
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    private fun startTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.status == FocusTimerStatus.Running) {
                delay(1000L)
                val current = _uiState.value
                if (current.status != FocusTimerStatus.Running) return@launch

                val nextRemaining = (current.remainingSeconds - 1).coerceAtLeast(0)
                _uiState.value = current.copy(remainingSeconds = nextRemaining)

                if (nextRemaining == 0) {
                    completeSession(_uiState.value)
                    return@launch
                }
            }
        }
    }

    private fun completeSession(state: FocusUiState) {
        timerJob?.cancel()
        timerJob = null

        _uiState.value = state.copy(
            status = FocusTimerStatus.Completed,
            remainingSeconds = 0,
            message = "Hoàn thành phiên tập trung."
        )
        saveCompletedSession(_uiState.value)
    }

    private fun saveCompletedSession(state: FocusUiState) {
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để lưu phiên tập trung.")
                focusRepository.saveSession(
                    FocusSession(
                        userId = userId,
                        durationSeconds = state.totalSeconds,
                        elapsedSeconds = state.totalSeconds,
                        completed = true,
                        interrupted = false,
                        startedAt = state.startedAt ?: System.currentTimeMillis(),
                        endedAt = System.currentTimeMillis()
                    )
                )
                focusRepository.addCompletedStudySeconds(userId, state.totalSeconds)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    message = throwable.message ?: "Không thể lưu phiên tập trung."
                )
            }
        }
    }

    private fun saveInterruptedSession(state: FocusUiState) {
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để lưu phiên bị gián đoạn.")
                focusRepository.saveSession(
                    FocusSession(
                        userId = userId,
                        durationSeconds = state.totalSeconds,
                        elapsedSeconds = state.elapsedSeconds,
                        completed = false,
                        interrupted = true,
                        startedAt = state.startedAt ?: System.currentTimeMillis(),
                        endedAt = System.currentTimeMillis()
                    )
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    message = throwable.message ?: "Không thể lưu phiên bị gián đoạn."
                )
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
