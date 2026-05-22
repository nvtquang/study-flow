package com.example.studyflow.data.repository

import android.net.Uri
import com.example.studyflow.data.model.StudyFile
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class StorageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun uploadStudyFile(
        ownerUid: String,
        localUri: Uri,
        fileName: String,
        contentType: String = ""
    ): StudyFile {
        val documentRef = firestore.collection(FILES_COLLECTION).document()
        val storagePath = "users/$ownerUid/files/${documentRef.id}-$fileName"
        val storageRef = storage.reference.child(storagePath)

        val uploadTask = storageRef.putFile(localUri).awaitResult()
        val downloadUrl = storageRef.downloadUrl.awaitResult().toString()

        val file = StudyFile(
            id = documentRef.id,
            userId = ownerUid,
            name = fileName,
            storagePath = storagePath,
            downloadUrl = downloadUrl,
            contentType = contentType,
            sizeBytes = uploadTask.metadata?.sizeBytes ?: 0L
        )

        documentRef.set(file).awaitResult()
        return file
    }

    suspend fun getFiles(ownerUid: String): List<StudyFile> {
        val snapshot = firestore.collection(FILES_COLLECTION)
            .whereEqualTo("userId", ownerUid)
            .get()
            .awaitResult()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(StudyFile::class.java)
        }
    }

    private companion object {
        const val FILES_COLLECTION = "studyFiles"
    }
}
