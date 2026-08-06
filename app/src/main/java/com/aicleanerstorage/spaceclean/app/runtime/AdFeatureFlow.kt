package com.aicleanerstorage.spaceclean.app.runtime

import com.aicleanerstorage.spaceclean.app.commercial.executor.InterstitialExecutor
import com.aicleanerstorage.spaceclean.app.commercial.policy.FeatureEventAdPolicy
import com.aicleanerstorage.spaceclean.common.ads.InterstitialAdRunner
import com.aicleanerstorage.spaceclean.common.ads.once
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.FeatureGroup
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore

internal class AdFeatureFlow(
    interstitialAds: InterstitialAdRunner,
    private val cleanupHistory: CleanupHistoryStore,
    private val onOperationBusyChanged: (Boolean) -> Unit,
    private val analyticsReporter: AnalyticsReporter,
    private val adPolicy: FeatureEventAdPolicy = FeatureEventAdPolicy(),
) : FeatureFlow {
    private val executor = InterstitialExecutor(interstitialAds)

    override fun handleOperation(event: FeatureOperationEvent, onContinue: () -> Unit) {
        val continueOnce = once(onContinue)
        onOperationBusyChanged(event.operationBusyState())
        if (event.completedCleanup()) cleanupHistory.recordCompletedCleanup()
        analyticsReporter.report(AnalyticsEvent.FeatureOperation(event))
        val request = adPolicy.request(event)
        if (request == null) continueOnce() else executor.execute(request, continueOnce)
    }

    override fun exit(feature: FeatureKey, reason: FeatureExitReason, onContinue: () -> Unit) {
        onOperationBusyChanged(false)
        executor.execute(adPolicy.exitRequest(feature, reason), once(onContinue))
    }
}

private fun FeatureOperationEvent.operationBusyState(): Boolean =
    when (this) {
        is FeatureOperationEvent.ScanStarted, is FeatureOperationEvent.OperationStarted -> true
        is FeatureOperationEvent.ScanFinished, is FeatureOperationEvent.OperationFinished -> false
    }

private fun FeatureOperationEvent.completedCleanup(): Boolean =
    when (this) {
        is FeatureOperationEvent.ScanFinished -> !hasResult && feature in cleanupFeatures
        is FeatureOperationEvent.OperationFinished -> success && feature in cleanupFeatures && action in cleanupActions
        else -> false
    }

private val cleanupActions = setOf(OperationAction.CLEAN, OperationAction.DELETE, OperationAction.REMOVE_LOCATION)
private val cleanupFeatures = FeatureKey.entries.filterTo(mutableSetOf()) { it.group == FeatureGroup.FILES } +
    setOf(FeatureKey.JUNK_CLEAN, FeatureKey.ANTI_VIRUS, FeatureKey.NOTIFICATION_CLEANER, FeatureKey.WHATSAPP_CLEANER)
