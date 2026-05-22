package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.StudySchedule
import com.example.studyflow.data.model.StudyTask
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.ScheduleRepository
import com.example.studyflow.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddScheduleViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val scheduleRepository: ScheduleRepository = ScheduleRepository(),
    private val taskRepository: TaskRepository = TaskRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddScheduleUiState())
    val uiState: StateFlow<AddScheduleUiState> = _uiState.asStateFlow()

    fun selectType(type: AddEntryType) {
        _uiState.value = _uiState.value.copy(entryType = type, message = null)
    }

    fun updateTitle(value: String) {
        _uiState.value = _uiState.value.copy(title = value, message = null)
    }

    fun updateDate(value: String) {
        _uiState.value = _uiState.value.copy(date = value, message = null)
    }

    fun updateStartTime(value: String) {
        _uiState.value = _uiState.value.copy(startTime = value, message = null)
    }

    fun updateEndTime(value: String) {
        _uiState.value = _uiState.value.copy(endTime = value, message = null)
    }

    fun updateLocation(value: String) {
        _uiState.value = _uiState.value.copy(location = value, message = null)
    }

    fun updateNote(value: String) {
        _uiState.value = _uiState.value.copy(note = value, message = null)
    }

    fun save() {
        val current = _uiState.value
        val validationError = validate(current)
        if (validationError != null) {
            _uiState.value = current.copy(message = validationError)
            return
        }

        _uiState.value = current.copy(isSaving = true, message = null, saveSuccess = false)
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để lưu lịch trình.")
                if (current.entryType == AddEntryType.Schedule) {
                    scheduleRepository.addSchedule(
                        StudySchedule(
                            userId = userId,
                            title = current.title.trim(),
                            eventType = "Bài giảng",
                            date = current.date,
                            startTime = current.startTime,
                            endTime = current.endTime,
                            location = current.location.trim(),
                            note = current.note.trim()
                        )
                    )
                } else {
                    taskRepository.addTask(
                        StudyTask(
                            userId = userId,
                            title = current.title.trim(),
                            eventType = "Hạn chót",
                            date = current.date,
                            startTime = current.startTime,
                            endTime = current.endTime,
                            location = current.location.trim(),
                            note = current.note.trim()
                        )
                    )
                }
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    message = "Đã lưu thành công."
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = false,
                    message = throwable.message ?: "Không thể lưu dữ liệu."
                )
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    private fun validate(state: AddScheduleUiState): String? {
        return when {
            state.title.trim().isBlank() -> "Vui lòng nhập tên."
            state.date.isBlank() -> "Vui lòng chọn ngày."
            state.startTime.isBlank() -> "Vui lòng chọn giờ bắt đầu."
            state.endTime.isBlank() -> "Vui lòng chọn giờ kết thúc."
            state.location.trim().isBlank() -> "Vui lòng nhập địa điểm."
            else -> null
        }
    }
}
