package com.reivaj.clarity.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.reivaj.clarity.data.export.PdfGenerator
import com.reivaj.clarity.data.export.PdfFileHandler
import com.reivaj.clarity.data.local.database.ClarityDatabase
import com.reivaj.clarity.domain.util.IosSoundManager
import com.reivaj.clarity.domain.util.SoundManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

/**
 * iOS-specific Koin module for providing platform dependencies.
 */
actual val platformModule = module {
    /**
     * Provides a singleton instance of the Room database for iOS.
     */
    single<ClarityDatabase> {
        val dbFilePath = NSHomeDirectory() + "/Documents/clarity.db"
        Room.databaseBuilder<ClarityDatabase>(
            name = dbFilePath,
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    
    /**
     * Provides PDF generator for iOS.
     */
    single { PdfGenerator() }
    
    /**
     * Provides PDF file handler for iOS (save & share).
     */
    single { PdfFileHandler() }
}
