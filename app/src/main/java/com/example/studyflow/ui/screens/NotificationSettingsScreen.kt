package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studyflow.ui.components.SectionTitle
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.components.ToggleSettingItem
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.viewmodel.NotificationSettingsUiState

@Composable
fun NotificationSettingsScreen(
    state: NotificationSettingsUiState,
    onBackClick: () -> Unit,
    onClassRemindersChange: (Boolean) -> Unit,
    onDeadlineAlertsChange: (Boolean) -> Unit,
    onFocusRemindersChange: (Boolean) -> Unit,
    onGroupChatNotificationsChange: (Boolean) -> Unit,
    onDailySummaryChange: (Boolean) -> Unit,
    onSummaryTimeChange: (String) -> Unit,
    onRetry: () -> Unit,
    onMessageShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceTint)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onBackClick) { Text("<") }
                    Column {
                        Text("Cài đặt thông báo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Tùy chỉnh nhắc nhở học tập", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (state.isLoading) {
                item {
                    StudyCard {
                        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = SkyBlue)
                        }
                    }
                }
                return@LazyColumn
            }

            if (state.errorMessage != null) {
                item {
                    StudyCard {
                        Text("Không thể tải cài đặt", fontWeight = FontWeight.Bold)
                        Text(state.errorMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = onRetry) { Text("Thử lại") }
                    }
                }
            }

            item { SectionTitle("Cảnh báo học tập") }
            item {
                ToggleSettingItem(
                    title = "Nhắc nhở lớp học",
                    subtitle = "Thông báo trước giờ học",
                    checked = state.settings.classReminders,
                    onCheckedChange = onClassRemindersChange
                )
            }
            item {
                ToggleSettingItem(
                    title = "Cảnh báo hạn chót",
                    subtitle = "Nhắc deadline quan trọng",
                    checked = state.settings.deadlineAlerts,
                    onCheckedChange = onDeadlineAlertsChange
                )
            }

            item { SectionTitle("Tập trung & Cộng đồng") }
            item {
                ToggleSettingItem(
                    title = "Nhắc nhở Chế độ tập trung",
                    subtitle = "Gợi ý bắt đầu Pomodoro",
                    checked = state.settings.focusReminders,
                    onCheckedChange = onFocusRemindersChange
                )
            }
            item {
                ToggleSettingItem(
                    title = "Thông báo nhóm chat",
                    subtitle = "Tin nhắn mới từ nhóm học",
                    checked = state.settings.groupChatNotifications,
                    onCheckedChange = onGroupChatNotificationsChange
                )
            }

            item { SectionTitle("Báo cáo") }
            item {
                ToggleSettingItem(
                    title = "Bản tóm tắt học tập hằng ngày",
                    subtitle = "Gửi báo cáo vào ${state.settings.summaryTime}",
                    checked = state.settings.dailySummary,
                    onCheckedChange = onDailySummaryChange
                )
            }
            item {
                StudyCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Giờ nhận báo cáo", fontWeight = FontWeight.Bold)
                            Text(state.settings.summaryTime, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { showTimePicker = true }) {
                            Text("Đổi giờ")
                        }
                    }
                }
            }
        }

        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = state.settings.summaryTime,
            onDismiss = { showTimePicker = false },
            onConfirm = {
                onSummaryTimeChange(it)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val parts = initialTime.split(":")
    val timeState: TimePickerState = rememberTimePickerState(
        initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 20,
        initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm("%02d:%02d".format(timeState.hour, timeState.minute)) }) {
                Text("Chọn")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        },
        text = { TimePicker(state = timeState) }
    )
}
