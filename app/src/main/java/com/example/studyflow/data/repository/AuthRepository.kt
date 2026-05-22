package com.example.studyflow.data.repository

import com.example.studyflow.data.model.UserProfile
import com.example.studyflow.util.awaitResult
import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()
    private val userRepository by lazy { UserRepository() }

    fun currentUserId(): String? {
        return runCatching { auth.currentUser?.uid }.getOrNull()
    }

    fun isSignedIn(): Boolean {
        return runCatching { auth.currentUser != null }.getOrDefault(false)
    }

    suspend fun signIn(email: String, password: String): UserProfile {
        val result = auth.signInWithEmailAndPassword(email, password).awaitResult()
        val firebaseUser = requireNotNull(result.user) {
            "No authenticated user returned from Firebase."
        }
        return UserProfile(
            uid = firebaseUser.uid,
            email = firebaseUser.email.orEmpty(),
            displayName = firebaseUser.displayName.orEmpty(),
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }

    suspend fun register(email: String, password: String, displayName: String): UserProfile {
        val result = auth.createUserWithEmailAndPassword(email, password).awaitResult()
        val firebaseUser = requireNotNull(result.user) {
            "No registered user returned from Firebase."
        }
        val profile = UserProfile(
            uid = firebaseUser.uid,
            email = firebaseUser.email.orEmpty(),
            displayName = displayName
        )
        userRepository.saveUserProfile(profile)
        return profile
    }

    fun signOut() {
        runCatching { auth.signOut() }
    }
}
