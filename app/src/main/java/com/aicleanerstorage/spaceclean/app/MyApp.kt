package com.aicleanerstorage.spaceclean.app

import android.app.Application
import android.content.ComponentCallbacks2
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.di.dataModule
import com.aicleanerstorage.spaceclean.app.di.presentationModule
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.trimFileManagerBitmapCache
import com.mar2sdk.core.AppMod
import com.mar2sdk.core.Core
import com.mar2sdk.impl.AppOpenAdManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Core.init(this, if (BuildConfig.DEBUG) AppMod.DEBUG else AppMod.RELEASE)
        AppOpenAdManager.init(this)
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
