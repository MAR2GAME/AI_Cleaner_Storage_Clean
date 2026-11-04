package com.aicleanerstorage.spaceclean.app.runtime

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.*
import com.aicleanerstorage.spaceclean.app.analytics.AppAnalyticsReporter
import com.aicleanerstorage.spaceclean.app.commercial.AdInterceptor
import com.aicleanerstorage.spaceclean.app.commercial.AdvertiseAdDriver
import com.aicleanerstorage.spaceclean.common.ads.*
import com.aicleanerstorage.spaceclean.app.permission.PermissionInterceptor
import org.koin.compose.koinInject
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore

internal data class AppRuntimeBindings(
    val ads: AdDriver,
    val interstitials: AdInterceptor,
    val featureFlow: FeatureFlow,
    val analyticsReporter: AnalyticsReporter,
    val externalActivities: ExternalActivityTracker,
    val externalGuard: ExternalAppOpenGuard,
)

@Composable
internal fun rememberAppRuntimeBindings(
    context: Context,
    notificationPermissionFlowActive: Boolean,
    sessionCoordinator: AppSessionCoordinator,
    cleanupHistory: CleanupHistoryStore,
): AppRuntimeBindings {
    val permission: PermissionInterceptor = koinInject()
    val notificationActive by rememberUpdatedState(notificationPermissionFlowActive)
    val activity = context.findActivity()
    val scope = rememberCoroutineScope()
    val driver = remember(activity) { AdvertiseAdDriver(context, activityProvider = { activity }) }
    val externalGuard = remember(driver, scope) { ExternalAppOpenGuard(driver, scope) }
    val ads = remember(driver, sessionCoordinator, externalGuard, permission) {
        AdInterceptor(
            driver,
            canShow = { request ->
                val rejected = request.scene is AdScene.PermissionRejected
                val allowed = !sessionCoordinator.isActive(RuntimeBusyReason.FeatureOperation) &&
                    (rejected || (permission.request.value == null && !notificationActive && !sessionCoordinator.isActive(RuntimeBusyReason.Permission) && !externalGuard.isReturning))
                allowed
            },
            onBusyChanged = { sessionCoordinator.set(RuntimeBusyReason.Interstitial, it) },
        )
    }
    DisposableEffect(ads, externalGuard) { onDispose { ads.dispose(); externalGuard.dispose() } }
    val analytics = remember { AppAnalyticsReporter() }
    val tracker = remember(externalGuard) {
        ExternalActivityTracker(externalGuard::onLaunch, externalGuard::onLaunchCancelled, externalGuard::onHostResumed)
    }
    val flow = remember(ads, cleanupHistory, sessionCoordinator, analytics) {
        AppFeatureFlow(ads::show, cleanupHistory,
            { sessionCoordinator.set(RuntimeBusyReason.FeatureOperation, it) }, analytics)
    }
    return remember(driver, ads, flow, analytics, tracker, externalGuard) {
        AppRuntimeBindings(driver, ads, flow, analytics, tracker, externalGuard)
    }
}
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
