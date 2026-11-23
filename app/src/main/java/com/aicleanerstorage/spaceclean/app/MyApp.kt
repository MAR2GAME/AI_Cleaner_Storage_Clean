package com.aicleanerstorage.spaceclean.app

import android.app.Application
import android.content.ComponentCallbacks2
import com.aicleanerstorage.spaceclean.app.di.dataModule
import com.aicleanerstorage.spaceclean.app.di.presentationModule
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.trimFileManagerBitmapCache
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(dataModule, presentationModule)
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW || level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            trimFileManagerBitmapCache()
        }
    }
}
