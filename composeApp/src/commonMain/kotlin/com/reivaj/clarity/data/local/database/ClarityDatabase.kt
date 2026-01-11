package com.reivaj.clarity.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reivaj.clarity.data.local.dao.ChatDao
import com.reivaj.clarity.data.local.dao.EmaDao
import com.reivaj.clarity.data.local.dao.GameSessionDao
import com.reivaj.clarity.data.local.dao.PreferenceDao
import com.reivaj.clarity.data.local.entity.ChatMessageEntity
import com.reivaj.clarity.data.local.entity.EmaEntity
import com.reivaj.clarity.data.local.entity.GameSessionEntity
import com.reivaj.clarity.data.local.entity.PreferenceEntity

@Database(
    entities = [EmaEntity::class, GameSessionEntity::class, ChatMessageEntity::class, PreferenceEntity::class],
    version = 4, // Updated for Preferences
    exportSchema = false,
)
abstract class ClarityDatabase : RoomDatabase() {
    abstract fun emaDao(): EmaDao
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun chatDao(): ChatDao
    abstract fun preferenceDao(): PreferenceDao
}
