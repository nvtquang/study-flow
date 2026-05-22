package com.example.studyflow.viewmodel

import com.example.studyflow.data.model.StudySchedule
import com.example.studyflow.data.model.StudyTask
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PlannerUiState(
    val selectedDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val dateOptions: List<String> = nextSevenDays(),
    val schedules: List<StudySchedule> = emptyList(),
    val tasks: List<StudyTask> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null
)

private fun nextSevenDays(): List<String> {
    val today = LocalDate.now()
    return (0..6).map { offset ->
        today.plusDays(offset.toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}
