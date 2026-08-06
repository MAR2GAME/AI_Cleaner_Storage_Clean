package com.aicleanerstorage.spaceclean.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.di.dataModule
import com.aicleanerstorage.spaceclean.app.di.presentationModule
import com.aicleanerstorage.spaceclean.app.notification.NotificationDefaultsInitializer
import com.aicleanerstorage.spaceclean.app.startup.SdkInitializationCoordinator
import com.aicleanerstorage.spaceclean.common.ads.AdvertiseSdkAdapter
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsPreferences
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {
    private val initializationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    companion object {
        lateinit var instance: MyApp
            private set

        private const val TAG = "StorageCleanApp"
    }

    lateinit var sdkInitialization: SdkInitializationCoordinator
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        val koinApplication = startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(dataModule, presentationModule)
        }
        AnalyticsTracker.initialize(koinApplication.koin.get<AnalyticsPreferences>())
        sdkInitialization =
            SdkInitializationCoordinator(
                scope = initializationScope,
                advertiseInitializer = {
                    withContext(Dispatchers.Main.immediate) {
                        AdvertiseSdkAdapter.initialize(this@MyApp, BuildConfig.DEBUG)
                        AdvertiseSdkAdapter.preloadStartup(this@MyApp)
                    }
                },
                analyticsInitializer = {
                    withContext(Dispatchers.Main.immediate) {
                        ProcessLifecycleOwner.get().lifecycle.addObserver(AppAnalyticsLifecycleObserver)
                    }
                },
                notificationDefaultsInitializer = {
                    val result =
                        NotificationDefaultsInitializer(
                            awaitRemoteConfig = { AdvertiseSdkAdapter.awaitRemoteConfigInitialization() },
                            hasConfig = AdvertiseSdkAdapter::hasNotificationConfig,
                            hasContent = AdvertiseSdkAdapter::hasNotificationContent,
                            loadLocalConfig = { readRawResource(R.raw.notification_config) },
                            loadLocalContent = { readRawResource(R.raw.notification_content) },
                            updateConfig = AdvertiseSdkAdapter::updateNotificationConfig,
                            updateContent = AdvertiseSdkAdapter::updateNotificationContent,
                        ).initialize()
                    Log.d(
                        TAG,
                        "Advertise notification defaults ready: " +
                            "config=${result.configSource}, content=${result.contentSource}, " +
                            "remoteCompleted=${result.remoteConfigCompleted}",
                    )
                },
                logger = { message, throwable ->
                    if (throwable == null) Log.d(TAG, message) else Log.e(TAG, message, throwable)
                },
            ).also(SdkInitializationCoordinator::start)
    }

    private fun readRawResource(resourceId: Int): String =
        resources.openRawResource(resourceId)
            .bufferedReader()
            .use { it.readText() }

    private object AppAnalyticsLifecycleObserver : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) = AnalyticsTracker.onAppForeground()
        override fun onStop(owner: LifecycleOwner) = AnalyticsTracker.onAppBackground()
    }
}
