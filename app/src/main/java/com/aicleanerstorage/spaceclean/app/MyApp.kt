package com.aicleanerstorage.spaceclean.app

import android.app.Application
import com.aicleanerstorage.spaceclean.app.di.dataModule
import com.aicleanerstorage.spaceclean.app.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {
    companion object {
        lateinit var instance: MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(dataModule, presentationModule)
        }
    }
}
