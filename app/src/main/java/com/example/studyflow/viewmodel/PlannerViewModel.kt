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

    private fun loadForSelectedDate() {
        val selectedDate = _uiState.value.selectedDate
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để xem lịch trình.")
                val schedules = scheduleRepository.getSchedulesByDate(userId, selectedDate)
                val tasks = taskRepository.getTasksByDate(userId, selectedDate)
                schedules to tasks
            }.onSuccess { (schedules, tasks) ->
                val fallback = if (schedules.isEmpty() && tasks.isEmpty()) mockPlannerItems(selectedDate) else null
                _uiState.value = _uiState.value.copy(
                    schedules = fallback?.first ?: schedules,
                    tasks = fallback?.second ?: tasks,
                    isLoading = false,
                    isEmpty = schedules.isEmpty() && tasks.isEmpty(),
                    errorMessage = null
                )
            }.onFailure { throwable ->
                val fallback = mockPlannerItems(selectedDate)
                _uiState.value = _uiState.value.copy(
                    schedules = fallback.first,
                    tasks = fallback.second,
                    isLoading = false,
                    isEmpty = true,
                    errorMessage = throwable.message ?: "Không thể tải lịch trình."
                )
            }
        }
    }

    private fun mockPlannerItems(date: String): Pair<List<StudySchedule>, List<StudyTask>> {
        return listOf(
            StudySchedule(
                id = "mock-lecture",
                title = "Android Native",
                eventType = "Bài giảng",
                date = date,
                startTime = "08:00",
                endTime = "09:30",
                location = "Phòng A201",
                note = "Ôn Navigation Compose"
            ),
            StudySchedule(
                id = "mock-practice",
                title = "Thực hành Firebase",
                eventType = "Thực hành",
                date = date,
                startTime = "14:00",
                endTime = "15:30",
                location = "Lab 3",
                note = "Chuẩn bị google-services.json"
            )
        ) to listOf(
            StudyTask(
                id = "mock-midterm",
                title = "Kiểm tra giữa kỳ",
                eventType = "Giữa kỳ",
                date = date,
                startTime = "10:00",
                endTime = "11:00",
                location = "Phòng B105",
                note = "Mang laptop"
            ),
            StudyTask(
                id = "mock-deadline",
                title = "Nộp báo cáo StudyFlow",
                eventType = "Hạn chót",
                date = date,
                startTime = "21:00",
                endTime = "22:00",
                location = "Online",
                note = "Upload bản PDF"
            )
        )
    }
}
