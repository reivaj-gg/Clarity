package com.reivaj.clarity.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Enumeration of available cognitive game types.
 */
@Serializable
enum class GameType {
    GO_NO_GO,
    VISUOSPATIAL_GRID,
    SIMON_SEQUENCE,
    VISUAL_SEARCH
}

/**
 * Represents a completed game session.
 *
 * This data class captures the performance metrics for a specific game instance
 * and links it to the EMA (Ecological Momentary Assessment) context.
 *
 * @param id Unique identifier.
 * @param timestamp Time of completion.
 * @param gameType The type of game played.
 * @param difficultyLevel The difficulty reached or selected.
 * @param score The standardized score achieved.
 * @param accuracy Calculate accuracy (0.0 - 1.0).
 * @param reactionTimeMs Average reaction time in milliseconds (null if not applicable).
 * @param emaId ID of the associated EMA check-in.
 * @param isBaselineSession True if the user was in a "Baseline" state during this session.
 */
@Serializable
data class GameSession(
    val id: String,
    val timestamp: Instant = Clock.System.now(),
    val gameType: GameType,
    val difficultyLevel: Int,
    val score: Int, // Generic score
    val accuracy: Float, // 0.0 to 1.0
    val reactionTimeMs: Long? = null, // Average reaction time, if applicable
    
    val emaId: String? = null, // Link to the EMA taken before this session
    val isBaselineSession: Boolean = false // Cached from EMA
)
