package com.example.studyflow.data.repository

import com.example.studyflow.data.model.FocusSession
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FocusRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore()
) {
    suspend fun saveSession(session: FocusSession): FocusSession {
        val document = firestore.collection(FOCUS_SESSIONS_COLLECTION).document()
        val sessionWithId = session.copy(id = document.id)
        document.set(sessionWithId).awaitResult()
        return sessionWithId
    }

    suspend fun addCompletedStudySeconds(userId: String, seconds: Int) {
        if (seconds <= 0) return

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .update("totalFocusSeconds", FieldValue.increment(seconds.toLong()))
            .awaitResult()
    }

    private companion object {
        const val FOCUS_SESSIONS_COLLECTION = "focusSessions"
        const val USERS_COLLECTION = "users"
    }
}
