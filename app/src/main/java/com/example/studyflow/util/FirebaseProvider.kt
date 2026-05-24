package com.example.studyflow.util

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseProvider {
    fun auth(): FirebaseAuth {
        ensureConfigured()
        return FirebaseAuth.getInstance()
    }

    fun firestore(): FirebaseFirestore {
        ensureConfigured()
        return FirebaseFirestore.getInstance()
    }

    fun storage(): FirebaseStorage {
        ensureConfigured()
        return FirebaseStorage.getInstance()
    }

    private fun ensureConfigured() {
        if (runCatching { FirebaseApp.getInstance() }.isFailure) {
            throw IllegalStateException(
                "Firebase chua duoc cau hinh. Hay dat file google-services.json vao thu muc app/ va build lai ung dung."
            )
        }
    }
}
