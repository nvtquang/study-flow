package com.example.studyflow.data.repository

import com.example.studyflow.data.model.StudyTask
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore()
) {
    suspend fun getTasksByDate(userId: String, date: String): List<StudyTask> {
        val snapshot = firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .get()
            .awaitResult()

        return snapshot.documents
            .mapNotNull { document ->
                document.toTask()
            }
            .withLocalChanges()
            .sortedBy { it.startTime }
    }

    suspend fun getTasksByDateRange(userId: String, startDate: String, endDate: String): List<StudyTask> {
        val snapshot = firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .awaitResult()

        return snapshot.documents
            .mapNotNull { document ->
                document.toTask()
            }
            .withLocalChanges()
            .filter { it.date in startDate..endDate }
            .sortedWith(compareBy<StudyTask> { it.date }.thenBy { it.startTime })
    }

    suspend fun addTask(task: StudyTask): StudyTask {
        val document = firestore.collection(TASKS_COLLECTION).document()
        val taskWithId = task.copy(id = document.id)
        document.set(taskWithId.toFirestoreMap()).awaitResult()
        return taskWithId
    }

    suspend fun updateTask(task: StudyTask) {
        require(task.id.isNotBlank()) { "Task id is required." }
        firestore.collection(TASKS_COLLECTION)
            .document(task.id)
            .set(task.toFirestoreMap())
            .awaitResult()
    }

    suspend fun updateCompletion(taskId: String, isCompleted: Boolean) {
        require(taskId.isNotBlank()) { "Task id is required." }
        localTaskCompletionOverrides[taskId] = isCompleted
        firestore.collection(TASKS_COLLECTION)
            .document(taskId)
            .update(
                mapOf(
                    COMPLETED_FIELD to isCompleted,
                    LEGACY_COMPLETED_FIELD to isCompleted
                )
            )
            .awaitResult()
    }

    suspend fun deleteTask(taskId: String) {
        require(taskId.isNotBlank()) { "Task id is required." }
        localDeletedTaskIds.add(taskId)
        firestore.collection(TASKS_COLLECTION)
            .document(taskId)
            .delete()
            .awaitResult()
    }

    private companion object {
        const val TASKS_COLLECTION = "tasks"
        const val COMPLETED_FIELD = "completed"
        const val LEGACY_COMPLETED_FIELD = "isCompleted"
    }
}

private fun DocumentSnapshot.toTask(): StudyTask? {
    val task = toObject(StudyTask::class.java) ?: return null
    val data = data.orEmpty()
    val completed = (data["completed"] as? Boolean)
        ?: (data["isCompleted"] as? Boolean)
        ?: task.isCompleted
    return task.copy(
        id = task.id.ifBlank { id },
        isCompleted = completed
    )
}

private fun List<StudyTask>.withLocalChanges(): List<StudyTask> {
    return filterNot { it.id in localDeletedTaskIds }
        .map { task ->
            localTaskCompletionOverrides[task.id]
                ?.let { task.copy(isCompleted = it) }
                ?: task
        }
}

private fun StudyTask.toFirestoreMap(): Map<String, Any> {
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

private val localTaskCompletionOverrides = mutableMapOf<String, Boolean>()
private val localDeletedTaskIds = mutableSetOf<String>()
