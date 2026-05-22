package com.example.studyflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.StudyGoal
import com.example.studyflow.data.repository.AuthRepository
import com.example.studyflow.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val goalRepository: GoalRepository = GoalRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoalsUiState(isLoading = true))
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    fun loadGoals() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để xem mục tiêu.")
                goalRepository.getGoals(userId)
            }.onSuccess { goals ->
                _uiState.value = _uiState.value.copy(
                    goals = goals,
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Không thể tải mục tiêu."
                )
            }
        }
    }

    fun selectFilter(filter: GoalFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun addGoal(
        title: String,
        description: String,
        category: String,
        dueDate: String,
        isDaily: Boolean
    ) {
        val validation = validate(title, category, dueDate, isDaily)
        if (validation != null) {
            _uiState.value = _uiState.value.copy(message = validation)
            return
        }

        _uiState.value = _uiState.value.copy(isSaving = true, message = null)
        viewModelScope.launch {
            runCatching {
                val userId = authRepository.currentUserId()
                    ?: error("Bạn cần đăng nhập để thêm mục tiêu.")
                goalRepository.addGoal(
                    StudyGoal(
                        userId = userId,
                        title = title.trim(),
                        description = description.trim(),
                        category = category.trim(),
                        dueDate = dueDate.trim(),
                        isDaily = isDaily
                    )
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Đã thêm mục tiêu."
                )
                loadGoals()
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = throwable.message ?: "Không thể thêm mục tiêu."
                )
            }
        }
    }

    fun toggleGoal(goal: StudyGoal) {
        viewModelScope.launch {
            runCatching {
                goalRepository.updateGoalCompletion(goal.id, !goal.completed)
            }.onSuccess {
                loadGoals()
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    message = throwable.message ?: "Không thể cập nhật mục tiêu."
                )
            }
        }
    }

    fun deleteGoal(goal: StudyGoal) {
        viewModelScope.launch {
            runCatching {
                goalRepository.deleteGoal(goal.id)
            }.onSuccess {
                _uiState.value = _uiState.value.copy(message = "Đã xóa mục tiêu.")
                loadGoals()
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    message = throwable.message ?: "Không thể xóa mục tiêu."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    private fun validate(
        title: String,
        category: String,
        dueDate: String,
        isDaily: Boolean
    ): String? {
        return when {
            title.trim().isBlank() -> "Vui lòng nhập tên mục tiêu."
            category.trim().isBlank() -> "Vui lòng nhập danh mục."
            !isDaily && dueDate.trim().isBlank() -> "Vui lòng nhập hạn hoàn thành."
            else -> null
        }
    }
}
