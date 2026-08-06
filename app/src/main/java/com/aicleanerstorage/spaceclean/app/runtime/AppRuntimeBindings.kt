package com.aicleanerstorage.spaceclean.app.runtime

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.aicleanerstorage.spaceclean.app.analytics.AppAnalyticsReporter
import com.aicleanerstorage.spaceclean.app.commercial.AdRuntimeFacade
import com.aicleanerstorage.spaceclean.app.commercial.executor.InterstitialExecutor
import com.aicleanerstorage.spaceclean.common.ads.AdRuntime
import com.aicleanerstorage.spaceclean.common.ads.AdRuntimeState
import com.aicleanerstorage.spaceclean.common.ads.InterstitialAdRunner
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore

internal data class AppRuntimeBindings(
    val ads: AdRuntimeFacade,
    val interstitials: InterstitialAdRunner,
    val featureFlow: FeatureFlow,
    val analyticsReporter: AnalyticsReporter,
    val externalActivities: ExternalActivityTracker,
)

@Composable
internal fun rememberAppRuntimeBindings(
    context: Context,
    notificationPermissionFlowActive: Boolean,
    sessionCoordinator: AppSessionCoordinator,
    cleanupHistory: CleanupHistoryStore,
): AppRuntimeBindings {
    val activity = context.findActivity()
    val latestPermissionFlowActive = rememberUpdatedState(notificationPermissionFlowActive)
    val adRuntime = remember(activity, sessionCoordinator) {
        AdRuntime(
            activityProvider = { activity },
            stateProvider = {
                AdRuntimeState(
                    permissionFlowActive = latestPermissionFlowActive.value || sessionCoordinator.isActive(RuntimeBusyReason.Permission),
                    featureOperationActive = sessionCoordinator.isActive(RuntimeBusyReason.FeatureOperation),
                )
            },
            onInterstitialStateChanged = { active -> sessionCoordinator.set(RuntimeBusyReason.Interstitial, active) },
        )
    }
    val ads = remember(adRuntime) { AdRuntimeFacade(adRuntime) }
    val interstitials = remember(ads) { InterstitialExecutor(ads) }
    val analyticsReporter = remember { AppAnalyticsReporter() }
    val externalActivities = remember(ads) {
        ExternalActivityTracker(
            markLaunch = ads::markExternalActivityLaunch,
            cancelLaunch = ads::cancelExternalActivityLaunch,
            markReturn = ads::onHostResumed,
        )
    }
    val featureFlow = remember(interstitials, cleanupHistory, sessionCoordinator, analyticsReporter) {
        AdFeatureFlow(
            interstitialAds = interstitials,
            cleanupHistory = cleanupHistory,
            onOperationBusyChanged = { active -> sessionCoordinator.set(RuntimeBusyReason.FeatureOperation, active) },
            analyticsReporter = analyticsReporter,
        )
    }
    return remember(ads, interstitials, featureFlow, analyticsReporter, externalActivities) {
        AppRuntimeBindings(ads, interstitials, featureFlow, analyticsReporter, externalActivities)
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
