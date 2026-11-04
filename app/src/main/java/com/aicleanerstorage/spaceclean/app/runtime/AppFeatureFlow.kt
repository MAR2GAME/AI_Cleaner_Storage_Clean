package com.aicleanerstorage.spaceclean.app.runtime

import com.aicleanerstorage.spaceclean.app.commercial.policy.FeatureEventAdPolicy
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.FeatureGroup
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore

internal class AppFeatureFlow(
    private val showAd: suspend (InterstitialRequest) -> Unit,
    private val cleanupHistory: CleanupHistoryStore,
    private val onOperationBusyChanged: (Boolean) -> Unit,
    private val analyticsReporter: AnalyticsReporter,
    private val adPolicy: FeatureEventAdPolicy = FeatureEventAdPolicy(),
) : FeatureFlow {

    override fun recordOperation(event: FeatureOperationEvent) {
        onOperationBusyChanged(event.operationBusyState())
        if (event.completedCleanup()) cleanupHistory.recordCompletedCleanup()
        analyticsReporter.report(AnalyticsEvent.FeatureOperation(event))
    }

    override suspend fun handleOperation(event: FeatureOperationEvent) {
        currentCoroutineContext().ensureActive()
        recordOperation(event)
        val request = adPolicy.request(event)
        if (request != null) showAd(request)
        currentCoroutineContext().ensureActive()
    }

    override suspend fun exit(feature: FeatureKey, reason: FeatureExitReason) {
        currentCoroutineContext().ensureActive()
        onOperationBusyChanged(false)
        analyticsReporter.report(AnalyticsEvent.FeatureExit(feature, reason))
        showAd(adPolicy.exitRequest(feature, reason))
        currentCoroutineContext().ensureActive()
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
