package com.reivaj.clarity.data.remote

import com.reivaj.clarity.domain.model.AiCoachContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Service to interact with Gemini API via Ktor.
 */
class GeminiAiService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun generateResponse(userMessage: String, context: AiCoachContext): String {
        if (GeminiConfig.API_KEY == "Your_API_Key_Here") {
            return "Please add your Gemini API Key in GeminiConfig.kt to enable the AI Coach."
        }

        val prompt = buildPrompt(userMessage, context)
        val requestBody = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models"
        val modelName = GeminiConfig.MODEL_NAME.removePrefix("models/")
        
        return try {
            val response: GeminiResponse = client.post("$baseUrl/$modelName:generateContent?key=${GeminiConfig.API_KEY}") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            if (response.error != null) {
                return "Coach Error: ${response.error.message ?: "Unknown error"}"
            }

            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I'm having trouble thinking right now. Please try again."
        } catch (e: Exception) {
            e.printStackTrace()
            "Error connecting to AI Coach: ${e.message}"
        }
    }

    private fun buildPrompt(message: String, context: AiCoachContext): String {
        return """
            You are Clarity Coach, an AI assistant specialized in cognitive performance 
            and mental wellness. You analyze the user's cognitive training data and EMA 
            (Ecological Momentary Assessment) responses to provide personalized insights.
            
            USER CONTEXT:
            - Name: ${context.userName}
            - Recent Performance: ${context.performanceSummary}
            - Mood Trends: ${context.moodSummary}
            - Sleep Patterns: ${context.sleepSummary}
            - Current Streak: ${context.streak} days
            - Total Sessions: ${context.totalSessions}
            - Recent Activity: ${context.recentActivity.joinToString(", ")}

            Guidelines:
            - Be encouraging and supportive.
            - Answer the user's question directly.
            - Reference their actual data when relevant.
            - Keep responses concise (under 200 words).
            - Do not provide medical diagnoses.
            
            USER MESSAGE: $message
        """.trimIndent()
    }
}

@Serializable
data class GeminiRequest(val contents: List<Content>)

@Serializable
data class Content(val parts: List<Part>)

@Serializable
data class Part(val text: String)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val error: GeminiErrorObj? = null
)

@Serializable
data class GeminiErrorObj(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

@Serializable
data class Candidate(val content: Content?)
