package com.reivaj.clarity.data.repository

import com.reivaj.clarity.domain.model.EMA
import com.reivaj.clarity.domain.model.GameSession
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for application data.
 *
 * This repository manages the retrieval and persistence of:
 * - Ecological Momentary Assessments (EMA)
 * - Game Sessions
 * - User application state (e.g., check-in completion)
 */
interface ClarityRepository {
    // EMA
    /** Persists a new EMA record to storage. */
    suspend fun saveEMA(ema: EMA)

    /** Retrieves the most recently submitted EMA, or null if none exists. */
    suspend fun getRecentEMA(): EMA?

    /** Observes the list of all historical EMAs linked to the user. */
    fun getAllEMAs(): Flow<List<EMA>>
    
    // Game Sessions
    /** Saves a completed game session result. */
    suspend fun saveGameSession(session: GameSession)

    /** Observes the full history of game sessions. */
    fun getAllGameSessions(): Flow<List<GameSession>>
    
    // Analysis Helper
    /**
     * Helper to retrieve sessions paired with their corresponding EMAs.
     * Useful for correlation analysis (e.g., Sleep vs. Score).
     */
    suspend fun getSessionsWithEMA(): List<Pair<GameSession, EMA?>>

    // App State
    /** Observes whether the user has completed the mandatory daily check-in. */
    fun isCheckInCompleted(): Flow<Boolean>

    /** Updates the daily check-in completion status. */
    suspend fun setCheckInCompleted(completed: Boolean)
}
