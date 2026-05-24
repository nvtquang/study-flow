package com.example.studyflow.data.repository

import com.example.studyflow.data.model.UserProfile
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore()
) {
    suspend fun getUserProfile(uid: String): UserProfile? {
        val snapshot = firestore.collection(USERS_COLLECTION)
            .document(uid)
            .get()
            .awaitResult()

        return snapshot.toObject(UserProfile::class.java)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        firestore.collection(USERS_COLLECTION)
            .document(profile.uid)
            .set(profile)
            .awaitResult()
    }

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}
