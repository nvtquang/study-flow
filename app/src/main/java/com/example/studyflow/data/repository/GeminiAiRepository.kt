package com.example.studyflow.data.repository

import com.example.studyflow.BuildConfig
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GeminiAiRepository : AiRepository {
    override suspend fun ask(question: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        require(apiKey.isNotBlank()) {
            "Chua cau hinh GEMINI_API_KEY trong local.properties."
        }

        val connection = (URL(GEMINI_ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 20_000
            readTimeout = 40_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("X-goog-api-key", apiKey)
        }

        try {
            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(buildRequestBody(question).toString())
            }

            val statusCode = connection.responseCode
            val responseText = if (statusCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            }

            if (statusCode !in 200..299) {
                error(parseErrorMessage(responseText).ifBlank { "Gemini loi HTTP $statusCode." })
            }

            parseAnswer(responseText)
        } finally {
            connection.disconnect()
        }
    }

    private fun buildRequestBody(question: String): JSONObject {
        return JSONObject()
            .put(
                "systemInstruction",
                JSONObject().put(
                    "parts",
                    JSONArray().put(
                        JSONObject().put(
                            "text",
                            "You are StudyFlow AI, a concise Vietnamese study assistant. " +
                                "Help students summarize lessons, explain concepts, create quizzes, " +
                                "and suggest practical study plans. Keep answers clear and actionable."
                        )
                    )
                )
            )
            .put(
                "contents",
                JSONArray().put(
                    JSONObject()
                        .put("role", "user")
                        .put(
                            "parts",
                            JSONArray().put(JSONObject().put("text", question))
                        )
                )
            )
            .put(
                "generationConfig",
                JSONObject()
                    .put("temperature", 0.7)
                    .put("maxOutputTokens", 900)
            )
    }

    private fun parseAnswer(responseText: String): String {
        val response = JSONObject(responseText)
        val candidates = response.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val parts = firstCandidate
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?: JSONArray()

        val answer = buildString {
            for (index in 0 until parts.length()) {
                val text = parts.optJSONObject(index)?.optString("text").orEmpty()
                if (text.isNotBlank()) {
                    if (isNotBlank()) append("\n")
                    append(text)
                }
            }
        }.trim()

        return answer.ifBlank { "Gemini khong tra ve noi dung phu hop." }
    }

    private fun parseErrorMessage(responseText: String): String {
        if (responseText.isBlank()) return ""
        return runCatching {
            JSONObject(responseText)
                .optJSONObject("error")
                ?.optString("message")
                .orEmpty()
        }.getOrDefault("")
    }

    private companion object {
        const val GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
    }
}
