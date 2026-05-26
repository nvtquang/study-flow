package com.example.studyflow.data.repository

import com.example.studyflow.data.model.StudyGroup
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GroupRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore()
) {
    fun listenGroups(
        onResult: (List<StudyGroup>) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        return firestore.collection(GROUPS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val groups = snapshot?.documents
                    ?.mapNotNull { document ->
                        document.toObject(StudyGroup::class.java)?.let { group ->
                            if (group.id.isBlank()) group.copy(id = document.id) else group
                        }
                    }
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
            .let { document ->
                document.toObject(StudyGroup::class.java)?.let { group ->
                    if (group.id.isBlank()) group.copy(id = document.id) else group
                }
            }
    }

    suspend fun joinGroup(groupId: String, userId: String, userName: String) {
        require(groupId.isNotBlank()) { "Group id is required." }
        firestore.collection(GROUPS_COLLECTION)
            .document(groupId)
            .update(
                mapOf(
                    "memberIds" to FieldValue.arrayUnion(userId),
                    "memberNames" to FieldValue.arrayUnion(userName.ifBlank { "Ban" })
                )
            )
            .awaitResult()
    }

    suspend fun leaveGroup(groupId: String, userId: String, userName: String) {
        require(groupId.isNotBlank()) { "Group id is required." }
        firestore.collection(GROUPS_COLLECTION)
            .document(groupId)
            .update(
                mapOf(
                    "memberIds" to FieldValue.arrayRemove(userId),
                    "memberNames" to FieldValue.arrayRemove(userName.ifBlank { "Ban" })
                )
            )
            .awaitResult()
    }

    private companion object {
        const val GROUPS_COLLECTION = "groups"
    }
}
