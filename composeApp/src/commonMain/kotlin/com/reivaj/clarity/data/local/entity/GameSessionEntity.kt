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
    
    val emaId: String?,
    val isBaselineSession: Boolean
)
