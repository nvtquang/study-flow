package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyflow.ui.components.Avatar
import com.example.studyflow.ui.components.DeadlineCard
import com.example.studyflow.ui.components.EmptyStateCard
import com.example.studyflow.ui.components.PrimaryButton
import com.example.studyflow.ui.components.ProgressRing
import com.example.studyflow.ui.components.ScheduleCard
import com.example.studyflow.ui.components.SectionTitle
import com.example.studyflow.ui.components.SecondaryButton
import com.example.studyflow.ui.components.StatCard
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SoftLavender
import com.example.studyflow.viewmodel.DashboardTimelineItem
import com.example.studyflow.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    state: HomeUiState,
    onRetry: () -> Unit,
    onFocusClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onGroupsClick: () -> Unit
) {
    DemoScreenLayout {
        item {
            HomeHeader(
                displayName = state.profile?.displayName.orEmpty(),
                email = state.profile?.email.orEmpty()
            )
        }

        if (state.isLoading) {
            item {
                StudyCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SkyBlue)
                    }
                }
            }
            return@DemoScreenLayout
        }

        if (state.errorMessage != null) {
            item {
                StudyCard {
                    Text(
                        text = "Không thể tải hồ sơ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onRetry) {
                        Text("Thử lại")
                    }
                }
            }
        }

        if (state.isEmpty) {
            item {
                EmptyStateCard(
                    title = "Chưa có hồ sơ Firestore",
                    message = "StudyFlow vẫn hiển thị dữ liệu mẫu để bạn kiểm tra dashboard."
                )
            }
        }

        item { SectionTitle(title = "Khẩn cấp") }
        item {
            DeadlineCard(
                title = state.dashboard.urgentDeadline.title,
                dueText = state.dashboard.urgentDeadline.dueText,
                priority = state.dashboard.urgentDeadline.priority
            )
        }

        item {
            ScheduleCard(
                time = state.dashboard.nextSchedule.time,
                title = state.dashboard.nextSchedule.title,
                location = state.dashboard.nextSchedule.location
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Còn lại",
                    value = state.dashboard.remainingCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Nhiệm vụ",
                    value = state.dashboard.taskCount.toString(),
                    modifier = Modifier.weight(1f),
                    accentColor = Lavender
                )
            }
        }

        item { DailyOverviewCard(items = state.dashboard.timeline) }

        item { SectionTitle(title = "Hoạt động gần đây") }
        state.dashboard.recentActivities.forEach { activity ->
            item {
                ActivityRow(activity = activity)
            }
        }

        item {
            StudyProgressCard(
                state = state,
                onFocusClick = onFocusClick,
                onGoalsClick = onGoalsClick,
                onGroupsClick = onGroupsClick
            )
        }
    }
}

@Composable
private fun HomeHeader(
    displayName: String,
    email: String
) {
    StudyCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "StudyFlow",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SkyBlue
                )
                Text(
                    text = if (displayName.isBlank()) "Chào mừng trở lại" else "Xin chào, $displayName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (email.isNotBlank()) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Avatar(initials = displayName.initials())
            Spacer(Modifier.size(10.dp))
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SoftLavender),
                contentAlignment = Alignment.Center
            ) {
                Text("⚙", color = SkyBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DailyOverviewCard(items: List<DashboardTimelineItem>) {
    StudyCard {
        SectionTitle(title = "Daily Overview")
        items.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Lavender, CircleShape)
                )
                Spacer(Modifier.size(12.dp))
                Text(
                    text = item.time,
                    modifier = Modifier.size(width = 52.dp, height = 24.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = SkyBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: String) {
    StudyCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(SoftLavender, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = SkyBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(12.dp))
            Text(
                text = activity,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StudyProgressCard(
    state: HomeUiState,
    onFocusClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onGroupsClick: () -> Unit
) {
    StudyCard {
        SectionTitle(title = "Tiến độ")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(SkyBlue, Lavender)))
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .background(CardWhite.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressRing(
                        progress = state.dashboard.pomodoroProgress,
                        size = 74,
                        color = Color.White
                    )
                }
                Spacer(Modifier.size(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pomodoro hôm nay",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mục tiêu tuần: ${(state.dashboard.weeklyGoalProgress * 100).toInt()}%",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(Modifier.height(2.dp))
        PrimaryButton(text = "Học ngay", onClick = onFocusClick)
        SecondaryButton(text = "Mục tiêu", onClick = onGoalsClick)
        SecondaryButton(text = "Nhóm học tập", onClick = onGroupsClick)
    }
}

private fun String.initials(): String {
    val source = trim()
    if (source.isBlank()) return "SF"
    return source
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
}
