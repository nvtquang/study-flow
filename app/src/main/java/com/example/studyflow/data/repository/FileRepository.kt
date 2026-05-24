package com.example.studyflow.data.repository

import android.net.Uri
import com.example.studyflow.data.model.StudyFile
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FileRepository(
    private val storage: FirebaseStorage = FirebaseProvider.storage(),
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore()
) {
    suspend fun uploadFile(
        userId: String,
        localUri: Uri,
        fileName: String,
        contentType: String = ""
    ): StudyFile {
        val document = firestore.collection(FILES_COLLECTION).document()
        val safeFileName = fileName.ifBlank { "studyflow-file" }
        val storagePath = "users/$userId/files/${document.id}-$safeFileName"
        val storageRef = storage.reference.child(storagePath)
        val uploadResult = storageRef.putFile(localUri).awaitResult()
        val downloadUrl = storageRef.downloadUrl.awaitResult().toString()

        val file = StudyFile(
            id = document.id,
            userId = userId,
            name = safeFileName,
            storagePath = storagePath,
            downloadUrl = downloadUrl,
            contentType = contentType,
            sizeBytes = uploadResult.metadata?.sizeBytes ?: 0L
        )

        document.set(file).awaitResult()
        return file
    }

    suspend fun getFiles(userId: String): List<StudyFile> {
        val snapshot = firestore.collection(FILES_COLLECTION)
            .whereEqualTo("userId", userId)
            .get()
            .awaitResult()

        return snapshot.documents
            .mapNotNull { it.toObject(StudyFile::class.java) }
            .sortedByDescending { it.createdAt }
    }

    private companion object {
        const val FILES_COLLECTION = "files"
    }
}
