package com.example.studyflow.data.repository

import com.example.studyflow.data.model.ChatMessage
import com.example.studyflow.util.FirebaseProvider
import com.example.studyflow.util.awaitResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ChatRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore()
) {
    fun listenMessages(
        groupId: String,
        onResult: (List<ChatMessage>) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        return firestore.collection(GROUPS_COLLECTION)
            .document(groupId)
            .collection(MESSAGES_COLLECTION)
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents
                    ?.mapNotNull { it.toObject(ChatMessage::class.java) }
                    .orEmpty()
                onResult(messages)
            }
    }

    suspend fun sendMessage(message: ChatMessage) {
        val messageDocument = firestore.collection(GROUPS_COLLECTION)
            .document(message.groupId)
            .collection(MESSAGES_COLLECTION)
            .document()
        val messageWithId = message.copy(id = messageDocument.id)

        messageDocument.set(messageWithId).awaitResult()
        firestore.collection(GROUPS_COLLECTION)
            .document(message.groupId)
            .update(
                mapOf(
                    "lastMessage" to message.text,
                    "lastMessageAt" to message.createdAt
                )
            )
            .awaitResult()
    }

    private companion object {
        const val GROUPS_COLLECTION = "groups"
        const val MESSAGES_COLLECTION = "messages"
    }
}
