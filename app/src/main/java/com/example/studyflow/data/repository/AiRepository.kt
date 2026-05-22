package com.example.studyflow.data.repository

interface AiRepository {
    suspend fun ask(question: String): String
}
