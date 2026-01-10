package com.reivaj.clarity.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(): RoomDatabase.Builder<ClarityDatabase> {
    val dbFilePath = NSHomeDirectory() + "/clarity.db"
    return Room.databaseBuilder<ClarityDatabase>(
        name = dbFilePath,
        factory =  { ClarityDatabase::class.instantiateImpl() }
    )
}
