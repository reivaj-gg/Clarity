package com.reivaj.clarity.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reivaj.clarity.data.local.entity.EmaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ema: EmaEntity)

    @Query("SELECT * FROM ema_table ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestEma(): EmaEntity?
    
    @Query("SELECT * FROM ema_table ORDER BY timestamp DESC LIMIT 1")
    fun getLatestEmaFlow(): Flow<EmaEntity?>

    @Query("SELECT * FROM ema_table ORDER BY timestamp DESC")
    suspend fun getAllEmasSnapshot(): List<EmaEntity>

    @Query("SELECT * FROM ema_table ORDER BY timestamp DESC")
    fun getAllEmas(): Flow<List<EmaEntity>>
}
