package com.reivaj.clarity.domain.usecase

import com.reivaj.clarity.data.remote.GeminiAiService
import com.reivaj.clarity.data.repository.ClarityRepository
import com.reivaj.clarity.domain.model.ChatMessage
import kotlinx.datetime.Clock

class SendChatMessageUseCase(
    private val repository: ClarityRepository,
    private val buildAiContextUseCase: BuildAiContextUseCase,
    private val aiService: GeminiAiService
) {
    suspend operator fun invoke(userMessageText: String) {
        // 1. Save User Message
        val userMessage = ChatMessage(
            content = userMessageText,
            timestamp = Clock.System.now(),
            isUser = true
        )
        repository.saveChatMessage(userMessage)

        // 2. Build Context
        val context = buildAiContextUseCase()

        // 3. Call AI Service
        // We catch errors here to save an error message if needed, or let ViewModel handle it.
        // Better to save error message to persisted chat so user sees "Failed to reply".
        try {
            val aiResponseText = aiService.generateResponse(userMessageText, context)
            
            // 4. Save AI Response
            val aiMessage = ChatMessage(
                content = aiResponseText,
                timestamp = Clock.System.now(),
                isUser = false
            )
            repository.saveChatMessage(aiMessage)
        } catch (e: Exception) {
            val errorMessage = ChatMessage(
                content = "Sorry, I couldn't connect to the server. Please try again later.",
                timestamp = Clock.System.now(),
                isUser = false,
                isError = true
            )
            repository.saveChatMessage(errorMessage)
        }
    }
}
