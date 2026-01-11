package com.reivaj.clarity.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reivaj.clarity.data.local.dao.EmaDao
import com.reivaj.clarity.data.local.dao.GameSessionDao
import com.reivaj.clarity.data.local.entity.EmaEntity
import com.reivaj.clarity.data.local.entity.GameSessionEntity

@Database(
    entities = [EmaEntity::class, GameSessionEntity::class],
    version = 2, // Updated for analytics enhancement
    exportSchema = false,
)
abstract class ClarityDatabase : RoomDatabase() {
    abstract fun emaDao(): EmaDao
    abstract fun gameSessionDao(): GameSessionDao
}
