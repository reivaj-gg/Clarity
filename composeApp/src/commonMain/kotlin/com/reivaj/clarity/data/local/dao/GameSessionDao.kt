package com.reivaj.clarity.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reivaj.clarity.data.local.entity.GameSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: GameSessionEntity)

    @Query("SELECT * FROM game_session_table ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<GameSessionEntity>>

    @Query("SELECT * FROM game_session_table ORDER BY timestamp DESC")
    suspend fun getAllSessionsSnapshot(): List<GameSessionEntity>

    @Query("SELECT * FROM game_session_table WHERE gameType = :gameType ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSessionByType(gameType: String): GameSessionEntity?
}
