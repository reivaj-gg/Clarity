package com.reivaj.clarity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_session_table")
data class GameSessionEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long, // Epoch milliseconds
    
    val gameType: String, // Enum Name
    val difficultyLevel: Int,
    val score: Int,
    val accuracy: Float,
    val reactionTimeMs: Long?,
    
    // Advanced metrics for enhanced analytics
    val omissionErrors: Int = 0, // Missed responses (Go/No-Go)
    val commissionErrors: Int = 0, // False alarms (Go/No-Go)
    val reactionTimeVariability: Double = 0.0, // Std dev of RT (fatigue indicator)
    val userFeedback: String? = null, // Optional post-game feedback
    val sessionStartHour: Int = 0, // Hour of day (0-23) for circadian analysis
    
    val emaId: String?,
    val isBaselineSession: Boolean,
)
