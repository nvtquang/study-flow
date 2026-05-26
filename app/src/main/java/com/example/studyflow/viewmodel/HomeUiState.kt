package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.UserProfile
import com.example.studyflow.data.model.StudySchedule
import com.example.studyflow.data.model.StudyTask

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
        fun fromPlannerData(
            todaySchedules: List<StudySchedule>,
            todayTasks: List<StudyTask>,
            weekSchedules: List<StudySchedule>,
            weekTasks: List<StudyTask>
        ): HomeDashboardData {
            val todayEntries = todaySchedules.size + todayTasks.size
            val remainingEntries = todaySchedules.count { !it.isCompleted } + todayTasks.count { !it.isCompleted }
            val weekTotal = weekSchedules.size + weekTasks.size
            val weekCompleted = weekSchedules.count { it.isCompleted } + weekTasks.count { it.isCompleted }
            val nextSchedule = todaySchedules
                .filter { !it.isCompleted }
                .minByOrNull { it.startTime }
            val urgentDeadline = todayTasks
                .filter { !it.isCompleted }
                .minByOrNull { it.endTime.ifBlank { it.startTime } }
            val timeline = todaySchedules
                .sortedBy { it.startTime }
                .map { DashboardTimelineItem(it.startTime, it.title) }
            val recentActivities = buildList {
                add("Da hoan thanh $weekCompleted/$weekTotal muc trong tuan")
                if (remainingEntries == 0 && todayEntries > 0) {
                    add("Tat ca nhiem vu hom nay da hoan thanh")
                } else {
                    add("Con $remainingEntries muc can xu ly hom nay")
                }
                urgentDeadline?.let { add("Deadline gan nhat: ${it.title}") }
            }

            return HomeDashboardData(
                urgentDeadline = DashboardDeadline(
                    title = urgentDeadline?.title ?: "Khong co deadline",
                    dueText = urgentDeadline?.let { task ->
                        "Han: ${task.endTime.ifBlank { task.startTime }}"
                    } ?: "Hom nay",
                    priority = if (urgentDeadline == null) "On track" else "Gap"
                ),
                nextSchedule = DashboardSchedule(
                    time = nextSchedule?.startTime ?: "--:--",
                    title = nextSchedule?.title ?: "Khong co lich sap toi",
                    location = nextSchedule?.location.orEmpty()
                ),
                remainingCount = remainingEntries,
                taskCount = todayEntries,
                recentActivities = recentActivities,
                pomodoroProgress = 0.72f,
                weeklyGoalProgress = if (weekTotal == 0) 0f else weekCompleted.toFloat() / weekTotal,
                timeline = timeline
            )
        }

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
