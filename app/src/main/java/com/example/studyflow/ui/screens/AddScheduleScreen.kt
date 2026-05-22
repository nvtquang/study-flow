package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.studyflow.ui.components.PrimaryButton
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.StudyBorder
import com.example.studyflow.viewmodel.AddEntryType
import com.example.studyflow.viewmodel.AddScheduleUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    state: AddScheduleUiState,
    onTypeSelected: (AddEntryType) -> Unit,
    onTitleChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onSaved: () -> Unit,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    var pickingStartTime by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onSaved()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = com.example.studyflow.ui.theme.SurfaceTint
    ) { innerPadding ->
        DemoScreenLayout {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = innerPadding.calculateTopPadding()),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onBackClick) {
                        Text("< Quay lại")
                    }
                    Text(
                        text = "Thêm mới",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {
                IntroCard()
            }
            item {
                SegmentedTypeControl(
                    selected = state.entryType,
                    onSelected = onTypeSelected
                )
            }
            item {
                StudyCard {
                    FormField(
                        value = state.title,
                        onValueChange = onTitleChange,
                        label = if (state.entryType == AddEntryType.Schedule) "Tên môn học" else "Tên nhiệm vụ"
                    )
                    ReadOnlyPickerField(
                        value = state.date,
                        label = "Ngày",
                        onClick = { showDatePicker = true }
                    )
                    ReadOnlyPickerField(
                        value = state.startTime,
                        label = "Giờ bắt đầu",
                        onClick = { pickingStartTime = true }
                    )
                    ReadOnlyPickerField(
                        value = state.endTime,
                        label = "Giờ kết thúc",
                        onClick = { pickingStartTime = false }
                    )
                    FormField(
                        value = state.location,
                        onValueChange = onLocationChange,
                        label = "Địa điểm"
                    )
                    FormField(
                        value = state.note,
                        onValueChange = onNoteChange,
                        label = "Ghi chú",
                        singleLine = false
                    )
                    PrimaryButton(
                        text = if (state.isSaving) "Đang lưu..." else "Lưu",
                        onClick = onSaveClick
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.date.toEpochMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateChange(millis.toLocalDateString())
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Chọn")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    pickingStartTime?.let { isStart ->
        val currentValue = if (isStart) state.startTime else state.endTime
        TimePickerDialog(
            initialTime = currentValue,
            onDismiss = { pickingStartTime = null },
            onConfirm = { value ->
                if (isStart) onStartTimeChange(value) else onEndTimeChange(value)
                pickingStartTime = null
            }
        )
    }
}

@Composable
private fun IntroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(SkyBlue, Lavender)),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Sắp xếp việc học rõ ràng",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Thêm lịch học hoặc deadline để StudyFlow nhắc bạn đúng ngày.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun SegmentedTypeControl(
    selected: AddEntryType,
    onSelected: (AddEntryType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite, RoundedCornerShape(20.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AddEntryType.entries.forEach { type ->
            val isSelected = type == selected
            TextButton(
                onClick = { onSelected(type) },
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) SkyBlue else Color.Transparent,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Text(
                    text = type.label,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        shape = RoundedCornerShape(18.dp),
        colors = fieldColors()
    )
}

@Composable
private fun ReadOnlyPickerField(
    value: String,
    label: String,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = fieldColors()
        )
        TextButton(
            onClick = onClick,
            modifier = Modifier.matchParentSize()
        ) {
            Text("")
        }
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
    val state: TimePickerState = rememberTimePickerState(
        initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 8,
        initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm("%02d:%02d".format(state.hour, state.minute)) }) {
                Text("Chọn")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        },
        text = { TimePicker(state = state) }
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SkyBlue,
    unfocusedBorderColor = StudyBorder,
    focusedContainerColor = CardWhite,
    unfocusedContainerColor = CardWhite
)

private fun String.toEpochMillis(): Long {
    return runCatching {
        LocalDate.parse(this)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }.getOrElse {
        LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}

private fun Long.toLocalDateString(): String {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ISO_LOCAL_DATE)
}
