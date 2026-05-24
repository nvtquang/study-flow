package com.example.studyflow.data.repository

import com.example.studyflow.data.model.StudyGroup
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GroupRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore()
) {
    fun listenUserGroups(
        userId: String,
        onResult: (List<StudyGroup>) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        return firestore.collection(GROUPS_COLLECTION)
            .whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val groups = snapshot?.documents
                    ?.mapNotNull { it.toObject(StudyGroup::class.java) }
                    ?.sortedByDescending { it.lastMessageAt.takeIf { value -> value > 0L } ?: it.createdAt }
                    .orEmpty()
                onResult(groups)
            }
    }

    suspend fun createGroup(
        userId: String,
        userName: String,
        name: String,
        description: String
    ): StudyGroup {
        val document = firestore.collection(GROUPS_COLLECTION).document()
        val group = StudyGroup(
            id = document.id,
            name = name,
            description = description,
            subjectIcon = name.take(1).uppercase().ifBlank { "S" },
            memberIds = listOf(userId),
            memberNames = listOf(userName.ifBlank { "Bạn" }),
            createdBy = userId
        )

        document.set(group).awaitResult()
        return group
    }

    suspend fun getGroup(groupId: String): StudyGroup? {
        return firestore.collection(GROUPS_COLLECTION)
            .document(groupId)
            .get()
            .awaitResult()
            .toObject(StudyGroup::class.java)
    }

    private companion object {
        const val GROUPS_COLLECTION = "groups"
    }
}
