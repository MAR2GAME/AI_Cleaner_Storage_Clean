package com.aicleanerstorage.spaceclean.app

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aicleanerstorage.spaceclean.app.background.BackgroundBatterySamplingController
import com.aicleanerstorage.spaceclean.app.analytics.AppSessionAnalytics
import com.aicleanerstorage.spaceclean.app.notification.NotificationIntentRouteResolver
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchCoordinator
import com.aicleanerstorage.spaceclean.common.ads.AdvertiseSdkAdapter
import com.aicleanerstorage.spaceclean.common.ui.theme.StorageCleanAppTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private var persistentNotificationStartJob: Job? = null
    private val sessionAnalytics = AppSessionAnalytics()
    private val backgroundBatterySamplingController: BackgroundBatterySamplingController by inject()
    private val launchCoordinator =
        AppLaunchCoordinator(
            targetRouteResolver = NotificationIntentRouteResolver::targetRoute,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        launchCoordinator.onCreate(intent)
        sessionAnalytics.handleLaunchIntent(intent)
        backgroundBatterySamplingController.reconcile()

        setContent {
            StorageCleanAppTheme {
                AppRoot(
                    launchCoordinator = launchCoordinator,
                    onNotificationPermissionGranted = {
                        backgroundBatterySamplingController.reconcile()
                        ensureSdkPersistentNotificationRunning()
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launchCoordinator.onNewIntent(intent)
        sessionAnalytics.handleLaunchIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        backgroundBatterySamplingController.reconcile()
        ensureSdkPersistentNotificationRunning()
    }

    private fun ensureSdkPersistentNotificationRunning() {
        if (!AppConfig.hasPostNotificationsPermission(this)) return
        if (persistentNotificationStartJob?.isActive == true) return
        persistentNotificationStartJob =
            lifecycleScope.launch {
                val app = application as? MyApp
                if (app == null || !app.sdkInitialization.awaitAdvertiseReady()) {
                    Log.w(TAG, "SDK persistent notification start skipped: SDK not ready")
                    return@launch
                }
                if (!AppConfig.hasPostNotificationsPermission(this@MainActivity)) return@launch
                runCatching {
                    AdvertiseSdkAdapter.ensurePersistentNotificationServiceRunning(this@MainActivity)
                }.onFailure { error ->
                    Log.e(TAG, "SDK persistent notification service start failed", error)
                }
            }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}
