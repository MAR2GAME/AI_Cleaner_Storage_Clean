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
import com.mar2sdk.impl.AppOpenAdManager
import com.aicleanerstorage.spaceclean.app.root.AppRootView
import com.aicleanerstorage.spaceclean.base.designsystem.theme.StorageCleanAppTheme
import com.aicleanerstorage.spaceclean.domain.port.BatterySamplingControl
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val batterySamplingControl: BatterySamplingControl by inject()
    private val mediaDeletionRequester: ActivityMediaDeletionRequester by inject()
    private val notificationRoutes = NotificationRouteCoordinator()
    private var hostResumed = false
    private var currentRoute: String? = null
    private var pageAllowsForegroundAd = false
    private var operationPending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(intent, warmDelivery = false)
        mediaDeletionRequester.attach(activityResultRegistry)
        enableEdgeToEdge()
        batterySamplingControl.reconcile()

        setContent {
            StorageCleanAppTheme(themeKey = BuildConfig.PRODUCT_THEME_KEY) {
                AppRootView(
                    notificationRoutes = notificationRoutes,
                    onHostStateChanged = ::updateAdHostState,
                )
            }
        }
    }

    override fun onDestroy() {
        mediaDeletionRequester.detach(activityResultRegistry)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        hostResumed = true
        reportAdHostState()
        batterySamplingControl.reconcile()
        if (NotificationUtil.hasNotiAccess()) Mar2AdGateway.startForegroundNotificationIfAllowed()
    }

    override fun onPostResume() {
        super.onPostResume()
        hostResumed = true
        reportAdHostState()
    }

    override fun onPause() {
        hostResumed = false
        reportAdHostState()
        super.onPause()
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
        reportAdHostState()
    }

    private fun updateAdHostState(
        route: String?,
        pageAllowsAd: Boolean,
        operationPending: Boolean,
    ) {
        currentRoute = route
        pageAllowsForegroundAd = pageAllowsAd
        this.operationPending = operationPending
        reportAdHostState()
    }

    private fun reportAdHostState() {
        AppOpenAdManager.updateHostState(
            activity = this,
            isResumed = hostResumed && lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED),
            currentRoute = currentRoute,
            pageAllowsAd = pageAllowsForegroundAd,
            operationPending = operationPending || notificationRoutes.pending.value != null,
        )
    }
}
