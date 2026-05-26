package com.example.studyflow.data.repository

import com.example.studyflow.data.model.StudySchedule
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.DocumentSnapshot
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
            .mapNotNull { document ->
                document.toSchedule()
            }
            .withLocalChanges()
            .sortedBy { it.startTime }
    }

    suspend fun getSchedulesByDateRange(userId: String, startDate: String, endDate: String): List<StudySchedule> {
        val snapshot = firestore.collection(SCHEDULES_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .awaitResult()

        return snapshot.documents
            .mapNotNull { document ->
                document.toSchedule()
            }
            .withLocalChanges()
            .filter { it.date in startDate..endDate }
            .sortedWith(compareBy<StudySchedule> { it.date }.thenBy { it.startTime })
    }

    suspend fun addSchedule(schedule: StudySchedule): StudySchedule {
        val document = firestore.collection(SCHEDULES_COLLECTION).document()
        val scheduleWithId = schedule.copy(id = document.id)
        document.set(scheduleWithId.toFirestoreMap()).awaitResult()
        return scheduleWithId
    }

    suspend fun updateSchedule(schedule: StudySchedule) {
        require(schedule.id.isNotBlank()) { "Schedule id is required." }
        firestore.collection(SCHEDULES_COLLECTION)
            .document(schedule.id)
            .set(schedule.toFirestoreMap())
            .awaitResult()
    }

    suspend fun updateCompletion(scheduleId: String, isCompleted: Boolean) {
        require(scheduleId.isNotBlank()) { "Schedule id is required." }
        localScheduleCompletionOverrides[scheduleId] = isCompleted
        firestore.collection(SCHEDULES_COLLECTION)
            .document(scheduleId)
            .update(
                mapOf(
                    COMPLETED_FIELD to isCompleted,
                    LEGACY_COMPLETED_FIELD to isCompleted
                )
            )
            .awaitResult()
    }

    suspend fun deleteSchedule(scheduleId: String) {
        require(scheduleId.isNotBlank()) { "Schedule id is required." }
        localDeletedScheduleIds.add(scheduleId)
        firestore.collection(SCHEDULES_COLLECTION)
            .document(scheduleId)
            .delete()
            .awaitResult()
    }

    private companion object {
        const val SCHEDULES_COLLECTION = "schedules"
        const val COMPLETED_FIELD = "completed"
        const val LEGACY_COMPLETED_FIELD = "isCompleted"
    }
}

private fun DocumentSnapshot.toSchedule(): StudySchedule? {
    val schedule = toObject(StudySchedule::class.java) ?: return null
    val data = data.orEmpty()
    val completed = (data["completed"] as? Boolean)
        ?: (data["isCompleted"] as? Boolean)
        ?: schedule.isCompleted
    return schedule.copy(
        id = schedule.id.ifBlank { id },
        isCompleted = completed
    )
}

private fun List<StudySchedule>.withLocalChanges(): List<StudySchedule> {
    return filterNot { it.id in localDeletedScheduleIds }
        .map { schedule ->
            localScheduleCompletionOverrides[schedule.id]
                ?.let { schedule.copy(isCompleted = it) }
                ?: schedule
        }
}

private fun StudySchedule.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "userId" to userId,
        "title" to title,
        "eventType" to eventType,
        "date" to date,
        "startTime" to startTime,
        "endTime" to endTime,
        "location" to location,
        "note" to note,
        "completed" to isCompleted,
        "isCompleted" to isCompleted,
        "createdAt" to createdAt
    )
}

private val localScheduleCompletionOverrides = mutableMapOf<String, Boolean>()
private val localDeletedScheduleIds = mutableSetOf<String>()
