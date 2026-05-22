package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.model.StudySchedule
import com.example.studyflow.data.model.StudyTask
import com.example.studyflow.ui.components.EmptyStateCard
import com.example.studyflow.ui.components.SectionTitle
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SoftLavender
import com.example.studyflow.ui.theme.StudyBorder
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.viewmodel.PlannerUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PlannerScreen(
    state: PlannerUiState,
    onDateSelected: (String) -> Unit,
    onRetry: () -> Unit,
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTint)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 16.dp,
                end = 20.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PlannerHeader()
            }
            item {
                DateSelector(
                    dates = state.dateOptions,
                    selectedDate = state.selectedDate,
                    onDateSelected = onDateSelected
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
                return@LazyColumn
            }

            if (state.errorMessage != null) {
                item {
                    StudyCard {
                        Text("Không thể tải lịch trình", fontWeight = FontWeight.Bold)
                        Text(
                            state.errorMessage,
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
                        title = "Chưa có dữ liệu thật",
                        message = "StudyFlow đang hiển thị dữ liệu mẫu cho ngày này."
                    )
                }
            }

            item { SectionTitle(title = "Lịch trình hôm nay") }
            items(state.schedules) { schedule ->
                ScheduleEventCard(schedule = schedule)
            }
            items(state.tasks) { task ->
                TaskEventCard(task = task)
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = SkyBlue,
            contentColor = Color.White
        ) {
            Text("+", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PlannerHeader() {
    StudyCard {
        Text(
            text = "StudyFlow",
            style = MaterialTheme.typography.titleMedium,
            color = SkyBlue,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Lịch trình",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Theo dõi lịch học, bài kiểm tra và deadline trong ngày.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DateSelector(
    dates: List<String>,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(dates) { date ->
            val parsed = LocalDate.parse(date)
            val selected = date == selectedDate
            Column(
                modifier = Modifier
                    .size(width = 76.dp, height = 88.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (selected) SkyBlue else CardWhite)
                    .clickable { onDateSelected(date) }
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = parsed.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("vi")),
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = parsed.dayOfMonth.toString(),
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ScheduleEventCard(schedule: StudySchedule) {
    EventCard(
        eventType = schedule.eventType,
        title = schedule.title,
        time = "${schedule.startTime} - ${schedule.endTime}",
        location = schedule.location,
        note = schedule.note,
        accent = SkyBlue
    )
}

@Composable
private fun TaskEventCard(task: StudyTask) {
    EventCard(
        eventType = task.eventType,
        title = task.title,
        time = "${task.startTime} - ${task.endTime}",
        location = task.location,
        note = task.note,
        accent = Lavender
    )
}

@Composable
private fun EventCard(
    eventType: String,
    title: String,
    time: String,
    location: String,
    note: String,
    accent: Color
) {
    StudyCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accent.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(eventType.take(1), color = accent, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = eventType,
                    color = accent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        InfoLine(label = "Giờ", value = time)
        InfoLine(label = "Địa điểm", value = location)
        InfoLine(label = "Ghi chú", value = note.ifBlank { "Không có ghi chú" })
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.size(width = 70.dp, height = 22.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
