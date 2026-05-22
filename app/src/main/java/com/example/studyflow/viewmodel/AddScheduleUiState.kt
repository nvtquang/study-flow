package com.example.studyflow.viewmodel

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class AddEntryType(val label: String) {
    Schedule("Lịch học"),
    Deadline("Deadline")
}

data class AddScheduleUiState(
    val entryType: AddEntryType = AddEntryType.Schedule,
    val title: String = "",
    val date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val startTime: String = LocalTime.now().withSecond(0).withNano(0).format(DateTimeFormatter.ofPattern("HH:mm")),
    val endTime: String = LocalTime.now().plusHours(1).withSecond(0).withNano(0).format(DateTimeFormatter.ofPattern("HH:mm")),
    val location: String = "",
    val note: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val message: String? = null
)
