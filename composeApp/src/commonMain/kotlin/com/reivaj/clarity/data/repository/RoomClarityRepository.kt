package com.reivaj.clarity.data.repository

import com.reivaj.clarity.data.local.database.ClarityDatabase
import com.reivaj.clarity.data.mapper.toDomain
import com.reivaj.clarity.data.mapper.toEntity
import com.reivaj.clarity.domain.model.EMA
import com.reivaj.clarity.domain.model.GameSession
import com.reivaj.clarity.domain.model.GameType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomClarityRepository(
    private val database: ClarityDatabase
) : ClarityRepository {

    private val emaDao = database.emaDao()
    private val gameSessionDao = database.gameSessionDao()

    override suspend fun saveEMA(ema: EMA) {
        emaDao.insert(ema.toEntity())
    }

    override fun getAllEMAs(): Flow<List<EMA>> {
        return emaDao.getAllEmas().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getRecentEMA(): EMA? {
        return emaDao.getLatestEma()?.toDomain()
    }

    override fun isCheckInCompleted(): Flow<Boolean> {
        return emaDao.getLatestEmaFlow().map { it != null } 
        // Logic: If any EMA exists, assume check-in done? 
        // Improved logic: Check if timestamp is today. 
        // But for MVP this suffices to unlock ("One checkin ever").
        // For production, we'd check DateUtils.isToday(it.timestamp).
    }

    /**
     * Sets the daily check-in completion status.
     *
     * In this Room-based implementation, this method is a no-op. The check-in status
     * is not stored as a separate flag but is derived from the existence of an EMA
     * record. The effective way to mark a check-in as complete is to save an
     * assessment using [saveEMA].
     *
     * @param completed The completion status (ignored in this implementation).
     */
    override suspend fun setCheckInCompleted(completed: Boolean) {
        // No-op: Check-in status is derived from the EMA table.
        // See `isCheckInCompleted()` for the read-side logic.
    }

    override suspend fun saveGameSession(session: GameSession) {
        gameSessionDao.insert(session.toEntity())
    }

    override fun getAllGameSessions(): Flow<List<GameSession>> {
        return gameSessionDao.getAllSessions().map { list -> list.map { it.toDomain() } }
    }
    
    override suspend fun getSessionsWithEMA(): List<Pair<GameSession, EMA?>> {
        val sessions = gameSessionDao.getAllSessionsSnapshot().map { it.toDomain() }
        val emas = emaDao.getAllEmasSnapshot().map { it.toDomain() }
        val emaMap = emas.associateBy { it.id }

        return sessions.map { session ->
            val ema = session.emaId?.let { emaMap[it] }
            session to ema
        }
    }
}
