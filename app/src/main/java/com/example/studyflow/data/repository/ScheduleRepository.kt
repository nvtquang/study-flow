package com.example.studyflow.data.repository

import com.example.studyflow.data.model.StudySchedule
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FirebaseFirestore

class ScheduleRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore()
) {
    suspend fun getSchedulesByDate(userId: String, date: String): List<StudySchedule> {
        val snapshot = firestore.collection(SCHEDULES_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .get()
            .awaitResult()

        return snapshot.documents
            .mapNotNull { it.toObject(StudySchedule::class.java) }
            .sortedBy { it.startTime }
    }

    suspend fun addSchedule(schedule: StudySchedule): StudySchedule {
        val document = firestore.collection(SCHEDULES_COLLECTION).document()
        val scheduleWithId = schedule.copy(id = document.id)
        document.set(scheduleWithId).awaitResult()
        return scheduleWithId
    }

    private companion object {
        const val SCHEDULES_COLLECTION = "schedules"
    }
}
