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

    fun startEdit(
        entryId: String,
        entryType: AddEntryType,
        title: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String,
        note: String,
        isCompleted: Boolean
    ) {
        if (_uiState.value.isEditing) return
        _uiState.value = _uiState.value.copy(
            entryId = entryId,
            isEditing = true,
            entryType = entryType,
            title = title,
            date = date,
            startTime = startTime,
            endTime = endTime,
            location = location,
            note = note,
            isCompleted = isCompleted,
            message = null
        )
    }

    fun selectType(type: AddEntryType) {
        if (_uiState.value.isEditing) return
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
                    ?: error("Ban can dang nhap de luu lich trinh.")

                if (current.entryType == AddEntryType.Schedule) {
                    val schedule = StudySchedule(
                        id = current.entryId,
                        userId = userId,
                        title = current.title.trim(),
                        eventType = "Bai giang",
                        date = current.date,
                        startTime = current.startTime,
                        endTime = current.endTime,
                        location = current.location.trim(),
                        note = current.note.trim(),
                        isCompleted = current.isCompleted
                    )
                    if (current.isEditing) {
                        scheduleRepository.updateSchedule(schedule)
                    } else {
                        scheduleRepository.addSchedule(schedule.copy(id = ""))
                    }
                } else {
                    val task = StudyTask(
                        id = current.entryId,
                        userId = userId,
                        title = current.title.trim(),
                        eventType = "Han chot",
                        date = current.date,
                        startTime = current.startTime,
                        endTime = current.endTime,
                        location = current.location.trim(),
                        note = current.note.trim(),
                        isCompleted = current.isCompleted
                    )
                    if (current.isEditing) {
                        taskRepository.updateTask(task)
                    } else {
                        taskRepository.addTask(task.copy(id = ""))
                    }
                }
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    message = "Da luu thanh cong."
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = false,
                    message = throwable.message ?: "Khong the luu du lieu."
                )
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    private fun validate(state: AddScheduleUiState): String? {
        return when {
            state.title.trim().isBlank() -> "Vui long nhap ten."
            state.date.isBlank() -> "Vui long chon ngay."
            state.startTime.isBlank() -> "Vui long chon gio bat dau."
            state.endTime.isBlank() -> "Vui long chon gio ket thuc."
            state.location.trim().isBlank() -> "Vui long nhap dia diem."
            else -> null
        }
    }
}
