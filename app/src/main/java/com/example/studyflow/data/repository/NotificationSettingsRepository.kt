package com.example.studyflow.data.repository

import com.example.studyflow.data.model.NotificationSettings
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FirebaseFirestore

class NotificationSettingsRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore()
) {
    suspend fun getSettings(userId: String): NotificationSettings {
        val snapshot = firestore.collection(SETTINGS_COLLECTION)
            .document(userId)
            .get()
            .awaitResult()

        return snapshot.toObject(NotificationSettings::class.java)
            ?: NotificationSettings(userId = userId)
    }

    suspend fun saveSettings(settings: NotificationSettings) {
        firestore.collection(SETTINGS_COLLECTION)
            .document(settings.userId)
            .set(settings)
            .awaitResult()
    }

    private companion object {
        const val SETTINGS_COLLECTION = "notificationSettings"
    }
}
