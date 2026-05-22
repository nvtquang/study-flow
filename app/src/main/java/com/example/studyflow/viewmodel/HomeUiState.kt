package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.UserProfile

data class HomeUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val dashboard: HomeDashboardData = HomeDashboardData.mock(),
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)

data class HomeDashboardData(
    val urgentDeadline: DashboardDeadline,
    val nextSchedule: DashboardSchedule,
    val remainingCount: Int,
    val taskCount: Int,
    val recentActivities: List<String>,
    val pomodoroProgress: Float,
    val weeklyGoalProgress: Float,
    val timeline: List<DashboardTimelineItem>
) {
    companion object {
        fun mock(): HomeDashboardData = HomeDashboardData(
            urgentDeadline = DashboardDeadline(
                title = "Nộp bài Android MVVM",
                dueText = "Hạn: 22:00 hôm nay",
                priority = "Gấp"
            ),
            nextSchedule = DashboardSchedule(
                time = "14:30",
                title = "Thảo luận nhóm StudyFlow",
                location = "Google Meet"
            ),
            remainingCount = 3,
            taskCount = 8,
            recentActivities = listOf(
                "Hoàn thành 1 phiên Pomodoro",
                "Đã thêm tài liệu Firebase Notes",
                "Cập nhật mục tiêu học Kotlin"
            ),
            pomodoroProgress = 0.72f,
            weeklyGoalProgress = 0.64f,
            timeline = listOf(
                DashboardTimelineItem("08:00", "Ôn Kotlin Coroutines"),
                DashboardTimelineItem("10:00", "Đọc tài liệu Firebase Auth"),
                DashboardTimelineItem("14:30", "Họp nhóm StudyFlow")
            )
        )
    }
}

data class DashboardDeadline(
    val title: String,
    val dueText: String,
    val priority: String
)

data class DashboardSchedule(
    val time: String,
    val title: String,
    val location: String
)

data class DashboardTimelineItem(
    val time: String,
    val title: String
)
