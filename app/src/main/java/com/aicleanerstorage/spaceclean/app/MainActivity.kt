package com.aicleanerstorage.spaceclean.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.permission.ActivityMediaDeletionRequester
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdGateway
import com.aicleanerstorage.spaceclean.app.integration.NotificationRouteCoordinator
import com.mar2sdk.core.notify.NotificationUtil
import com.aicleanerstorage.spaceclean.app.root.AppRootView
import com.aicleanerstorage.spaceclean.base.designsystem.theme.StorageCleanAppTheme
import com.aicleanerstorage.spaceclean.domain.port.BatterySamplingControl
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val batterySamplingControl: BatterySamplingControl by inject()
    private val mediaDeletionRequester: ActivityMediaDeletionRequester by inject()
    private val notificationRoutes = NotificationRouteCoordinator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(intent, warmDelivery = false)
        mediaDeletionRequester.attach(activityResultRegistry)
        enableEdgeToEdge()
        batterySamplingControl.reconcile()

        setContent {
            StorageCleanAppTheme(themeKey = BuildConfig.PRODUCT_THEME_KEY) {
                AppRootView(notificationRoutes)
            }
        }
    }

    override fun onDestroy() {
        mediaDeletionRequester.detach(activityResultRegistry)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        batterySamplingControl.reconcile()
        if (NotificationUtil.hasNotiAccess()) Mar2AdGateway.startForegroundNotificationIfAllowed()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent, warmDelivery = true)
    }

    private fun handleNotificationIntent(intent: android.content.Intent?, warmDelivery: Boolean) {
        if (intent == null) return
        NotificationUtil.trackAppOpen(intent)
        notificationRoutes.accept(intent, warmDelivery)
    }
}
