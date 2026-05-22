package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.StudyGoal

enum class GoalFilter(val label: String) {
    Active("Đang thực hiện"),
    Completed("Đã hoàn thành")
}

data class GoalsUiState(
    val goals: List<StudyGoal> = emptyList(),
    val selectedFilter: GoalFilter = GoalFilter.Active,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null
) {
    val completedCount: Int
        get() = goals.count { it.completed }

    val totalCount: Int
        get() = goals.size

    val progress: Float
        get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount.toFloat()

    val visibleGoals: List<StudyGoal>
        get() = when (selectedFilter) {
            GoalFilter.Active -> goals.filterNot { it.completed }
            GoalFilter.Completed -> goals.filter { it.completed }
        }
}
