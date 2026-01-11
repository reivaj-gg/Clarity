package com.reivaj.clarity.android

import android.app.Application
import com.reivaj.clarity.di.appModule
import com.reivaj.clarity.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@AndroidApp)
            modules(appModule, platformModule)
        }
    }
}
