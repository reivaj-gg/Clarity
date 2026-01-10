package com.reivaj.clarity.android

import android.app.Application
import com.reivaj.clarity.di.appModule
import com.reivaj.clarity.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ClarityApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin activity
            androidLogger()
            // Pass the Android context to Koin
            androidContext(this@ClarityApplication)
            // Load both the common and platform-specific Koin modules
            modules(appModule, platformModule)
        }
    }
}
