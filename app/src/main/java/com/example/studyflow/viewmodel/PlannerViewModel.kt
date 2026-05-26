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

class PlannerViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val scheduleRepository: ScheduleRepository = ScheduleRepository(),
    private val taskRepository: TaskRepository = TaskRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()
    private val localCompletedOverrides = mutableMapOf<String, Boolean>()
    private val localDeletedIds = mutableSetOf<String>()

    init {
        loadForSelectedDate()
    }

    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadForSelectedDate()
    }

    fun refresh() {
        loadForSelectedDate()
    }

    fun setCompletion(entryId: String, isSchedule: Boolean, isCompleted: Boolean) {
        localCompletedOverrides[entryId] = isCompleted
        applyCompletionToState(entryId, isSchedule, isCompleted)
        if (entryId.isMockId()) return

        viewModelScope.launch {
            runCatching {
                if (isSchedule) {
                    scheduleRepository.updateCompletion(entryId, isCompleted)
                } else {
                    taskRepository.updateCompletion(entryId, isCompleted)
                }
            }.onSuccess {
                loadForSelectedDate()
            }.onFailure { throwable ->
                localCompletedOverrides.remove(entryId)
                applyCompletionToState(entryId, isSchedule, !isCompleted)
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.message ?: "Khong the cap nhat trang thai."
                )
            }
        }
    }

    fun deleteEntry(entryId: String, isSchedule: Boolean) {
        localDeletedIds.add(entryId)
        removeEntryFromState(entryId, isSchedule)
        if (entryId.isMockId()) return

        viewModelScope.launch {
            runCatching {
                if (isSchedule) {
                    scheduleRepository.deleteSchedule(entryId)
                } else {
                    taskRepository.deleteTask(entryId)
                }
            }.onSuccess {
                loadForSelectedDate()
            }.onFailure { throwable ->
                localDeletedIds.remove(entryId)
                loadForSelectedDate()
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.message ?: "Khong the xoa muc nay."
                )
            }
        }
    }

    private fun loadForSelectedDate() {
        val selectedDate = _uiState.value.selectedDate
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Ban can dang nhap de xem lich trinh.")
                val schedules = scheduleRepository.getSchedulesByDate(userId, selectedDate)
                val tasks = taskRepository.getTasksByDate(userId, selectedDate)
                schedules to tasks
            }.onSuccess { (schedules, tasks) ->
                val fallback = if (schedules.isEmpty() && tasks.isEmpty()) {
                    mockPlannerItems(selectedDate)
                } else {
                    null
                }
                val nextSchedules = applyLocalScheduleState(fallback?.first ?: schedules)
                val nextTasks = applyLocalTaskState(fallback?.second ?: tasks)
                _uiState.value = _uiState.value.copy(
                    schedules = nextSchedules,
                    tasks = nextTasks,
                    isLoading = false,
                    isEmpty = nextSchedules.isEmpty() && nextTasks.isEmpty(),
                    errorMessage = null
                )
            }.onFailure { throwable ->
                val fallback = mockPlannerItems(selectedDate)
                val nextSchedules = applyLocalScheduleState(fallback.first)
                val nextTasks = applyLocalTaskState(fallback.second)
                _uiState.value = _uiState.value.copy(
                    schedules = nextSchedules,
                    tasks = nextTasks,
                    isLoading = false,
                    isEmpty = nextSchedules.isEmpty() && nextTasks.isEmpty(),
                    errorMessage = throwable.message ?: "Khong the tai lich trinh."
                )
            }
        }
    }

    private fun applyCompletionToState(entryId: String, isSchedule: Boolean, isCompleted: Boolean) {
        val current = _uiState.value
        _uiState.value = if (isSchedule) {
            current.copy(
                schedules = current.schedules.map {
                    if (it.id == entryId) it.copy(isCompleted = isCompleted) else it
                },
                errorMessage = null
            )
        } else {
            current.copy(
                tasks = current.tasks.map {
                    if (it.id == entryId) it.copy(isCompleted = isCompleted) else it
                },
                errorMessage = null
            )
        }
    }

    private fun removeEntryFromState(entryId: String, isSchedule: Boolean) {
        val current = _uiState.value
        _uiState.value = if (isSchedule) {
            current.copy(
                schedules = current.schedules.filterNot { it.id == entryId },
                errorMessage = null
            )
        } else {
            current.copy(
                tasks = current.tasks.filterNot { it.id == entryId },
                errorMessage = null
            )
        }
    }

    private fun applyLocalScheduleState(schedules: List<StudySchedule>): List<StudySchedule> {
        return schedules
            .filterNot { it.id in localDeletedIds }
            .map { schedule ->
                localCompletedOverrides[schedule.id]?.let { schedule.copy(isCompleted = it) } ?: schedule
            }
    }

    private fun applyLocalTaskState(tasks: List<StudyTask>): List<StudyTask> {
        return tasks
            .filterNot { it.id in localDeletedIds }
            .map { task ->
                localCompletedOverrides[task.id]?.let { task.copy(isCompleted = it) } ?: task
            }
    }

    private fun mockPlannerItems(date: String): Pair<List<StudySchedule>, List<StudyTask>> {
        return listOf(
            StudySchedule(
                id = "mock-lecture",
                title = "Android Native",
                eventType = "Bai giang",
                date = date,
                startTime = "08:00",
                endTime = "09:30",
                location = "Phong A201",
                note = "On Navigation Compose"
            ),
            StudySchedule(
                id = "mock-practice",
                title = "Thuc hanh Firebase",
                eventType = "Thuc hanh",
                date = date,
                startTime = "14:00",
                endTime = "15:30",
                location = "Lab 3",
                note = "Chuan bi google-services.json"
            )
        ) to listOf(
            StudyTask(
                id = "mock-midterm",
                title = "Kiem tra giua ky",
                eventType = "Giua ky",
                date = date,
                startTime = "10:00",
                endTime = "11:00",
                location = "Phong B105",
                note = "Mang laptop"
            ),
            StudyTask(
                id = "mock-deadline",
                title = "Nop bao cao StudyFlow",
                eventType = "Han chot",
                date = date,
                startTime = "21:00",
                endTime = "22:00",
                location = "Online",
                note = "Upload ban PDF"
            )
        )
    }

    private fun String.isMockId(): Boolean = startsWith("mock-")
}
