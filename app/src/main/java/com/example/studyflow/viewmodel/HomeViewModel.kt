package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.ScheduleRepository
import com.example.studyflow.data.repository.TaskRepository
import com.example.studyflow.data.repository.UserRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val scheduleRepository: ScheduleRepository = ScheduleRepository(),
    private val taskRepository: TaskRepository = TaskRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            isEmpty = false,
            dashboard = HomeDashboardData.mock()
        )

        viewModelScope.launch {
            runCatching {
                val uid = authRepository.currentUserId()
                if (uid.isNullOrBlank()) {
                    HomeLoadResult()
                } else {
                    val today = LocalDate.now()
                    val startOfWeek = today.minusDays((today.dayOfWeek.value - 1).toLong())
                    val endOfWeek = startOfWeek.plusDays(6)
                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                    val todayText = today.format(formatter)
                    val weekStartText = startOfWeek.format(formatter)
                    val weekEndText = endOfWeek.format(formatter)
                    val profile = userRepository.getUserProfile(uid)
                    val todaySchedules = scheduleRepository.getSchedulesByDate(uid, todayText)
                    val todayTasks = taskRepository.getTasksByDate(uid, todayText)
                    val weekSchedules = scheduleRepository.getSchedulesByDateRange(uid, weekStartText, weekEndText)
                    val weekTasks = taskRepository.getTasksByDateRange(uid, weekStartText, weekEndText)
                    HomeLoadResult(
                        profile = profile,
                        dashboard = HomeDashboardData.fromPlannerData(
                            todaySchedules = todaySchedules,
                            todayTasks = todayTasks,
                            weekSchedules = weekSchedules,
                            weekTasks = weekTasks
                        )
                    )
                }
            }.onSuccess { result ->
                _uiState.value = HomeUiState(
                    isLoading = false,
                    profile = result.profile,
                    dashboard = result.dashboard,
                    isEmpty = result.profile == null
                )
            }.onFailure { throwable ->
                _uiState.value = HomeUiState(
                    isLoading = false,
                    dashboard = HomeDashboardData.mock(),
                    errorMessage = throwable.message ?: "Không thể tải dữ liệu người dùng."
                )
            }
        }
    }
}

private data class HomeLoadResult(
    val profile: com.example.studyflow.data.model.UserProfile? = null,
    val dashboard: HomeDashboardData = HomeDashboardData.mock()
)
