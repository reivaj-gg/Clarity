package com.reivaj.clarity.di

import androidx.room.Room
import com.reivaj.clarity.data.export.PdfGenerator
import com.reivaj.clarity.data.export.PdfFileHandler
import com.reivaj.clarity.data.local.database.ClarityDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module for providing platform dependencies.
 */
actual val platformModule = module {
    /**
     * Provides a singleton instance of the Room database.
     *
     * It uses the application context provided by Koin's `androidContext()`
     * to build the database, names it "clarity.db", and provides a fallback
     * for destructive migrations.
     */
    single<ClarityDatabase> {
        Room.databaseBuilder(
            androidContext(),
            ClarityDatabase::class.java,
            "clarity.db"
        ).fallbackToDestructiveMigration().build()
    }
    
    /**
     * Provides PDF generator for Android.
     */
    single { PdfGenerator(androidContext()) }
    
    /**
     * Provides PDF file handler for Android (save & share).
     */
    single { PdfFileHandler(androidContext()) }
}
