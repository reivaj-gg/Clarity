package com.reivaj.clarity.data.repository

import com.reivaj.clarity.domain.model.EMA
import com.reivaj.clarity.domain.model.GameSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An in-memory implementation of [ClarityRepository] for the contest MVP.
 * Data is lost when the app process is killed.
 *
 * Future improvements: Replace with Room or SQLDelight for persistence.
 */
class InMemoryClarityRepository : ClarityRepository {
    private val _emas = MutableStateFlow<List<EMA>>(emptyList())
    private val _sessions = MutableStateFlow<List<GameSession>>(emptyList())

    override suspend fun saveEMA(ema: EMA) {
        _emas.update { it + ema }
    }

    override suspend fun getRecentEMA(): EMA? {
        return _emas.value.maxByOrNull { it.timestamp }
    }

    override fun getAllEMAs(): Flow<List<EMA>> {
        return _emas.asStateFlow()
    }

    override suspend fun saveGameSession(session: GameSession) {
        _sessions.update { it + session }
    }

    override fun getAllGameSessions(): Flow<List<GameSession>> {
        return _sessions.asStateFlow()
    }

    override suspend fun getSessionsWithEMA(): List<Pair<GameSession, EMA?>> {
        val currentEmas = _emas.value
        val currentSessions = _sessions.value
        return currentSessions.map { session ->
            session to currentEmas.find { it.id == session.emaId }
        }
    }

    private val _isCheckInCompleted = MutableStateFlow(false)

    override fun isCheckInCompleted(): Flow<Boolean> {
        return _isCheckInCompleted.asStateFlow()
    }

    override suspend fun setCheckInCompleted(completed: Boolean) {
        _isCheckInCompleted.update { completed }
    }
}
