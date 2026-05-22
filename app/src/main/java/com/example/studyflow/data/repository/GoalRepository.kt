package com.example.studyflow.data.repository

import com.example.studyflow.data.model.StudyGoal
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FirebaseFirestore

class GoalRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getGoals(userId: String): List<StudyGoal> {
        val snapshot = firestore.collection(GOALS_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .awaitResult()

        return snapshot.documents
            .mapNotNull { it.toObject(StudyGoal::class.java) }
            .sortedByDescending { it.createdAt }
    }

    suspend fun addGoal(goal: StudyGoal): StudyGoal {
        val document = firestore.collection(GOALS_COLLECTION).document()
        val goalWithId = goal.copy(id = document.id)
        document.set(goalWithId).awaitResult()
        return goalWithId
    }

    suspend fun updateGoalCompletion(goalId: String, completed: Boolean) {
        firestore.collection(GOALS_COLLECTION)
            .document(goalId)
            .update(
                mapOf(
                    "completed" to completed,
                    "completedAt" to if (completed) System.currentTimeMillis() else null
                )
            )
            .awaitResult()
    }

    suspend fun deleteGoal(goalId: String) {
        firestore.collection(GOALS_COLLECTION)
            .document(goalId)
            .delete()
            .awaitResult()
    }

    private companion object {
        const val GOALS_COLLECTION = "goals"
    }
}
