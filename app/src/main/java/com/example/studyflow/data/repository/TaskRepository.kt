package com.example.studyflow.data.repository

import com.example.studyflow.data.model.StudyTask
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getTasksByDate(userId: String, date: String): List<StudyTask> {
        val snapshot = firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .get()
            .awaitResult()

        return snapshot.documents
            .mapNotNull { it.toObject(StudyTask::class.java) }
            .sortedBy { it.startTime }
    }

    suspend fun addTask(task: StudyTask): StudyTask {
        val document = firestore.collection(TASKS_COLLECTION).document()
        val taskWithId = task.copy(id = document.id)
        document.set(taskWithId).awaitResult()
        return taskWithId
    }

    private companion object {
        const val TASKS_COLLECTION = "tasks"
    }
}
