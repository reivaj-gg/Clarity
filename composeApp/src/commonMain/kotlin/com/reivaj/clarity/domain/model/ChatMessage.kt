package com.reivaj.clarity.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a chat message in the AI Coach interface.
 */
@Serializable
data class ChatMessage(
    val id: Long = 0,
    val content: String,
    val info: String = "",
    val timestamp: Instant,
    val isUser: Boolean, // true = User, false = AI Coach
    val isError: Boolean = false
)
