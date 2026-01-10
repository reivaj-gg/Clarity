package com.reivaj.clarity.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// We need a way to access Context in this function. 
// Standard KMP pattern is typically passing context to the create function 
// or using a Koin component if the function itself can retrieve it.
//
// However, 'expect fun' cannot have arguments that differ from 'actual fun'.
// So we must access Context via a global provider or Dependency Injection helper.
//
// A common workaround in KMP for context-less expects:
// Use a 'KoinComponent' object or hold a static reference initialized at app startup.

object AndroidContextUtils : KoinComponent {
    val context: Context by inject()
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<ClarityDatabase> {
    val appContext = AndroidContextUtils.context.applicationContext
    val dbFile = appContext.getDatabasePath("clarity.db")
    return Room.databaseBuilder<ClarityDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
