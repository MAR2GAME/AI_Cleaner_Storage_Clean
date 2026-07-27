package com.aicleanerstorage.spaceclean.app.runtime

import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.FeatureGroup
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore

internal class LocalFeatureFlow(
    private val cleanupHistory: CleanupHistoryStore,
    private val onOperationBusyChanged: (Boolean) -> Unit,
) : FeatureFlow {
    override fun handleOperation(event: FeatureOperationEvent, onContinue: () -> Unit) {
        onOperationBusyChanged(event.operationBusyState())
        if (event.completedCleanup()) cleanupHistory.recordCompletedCleanup()
        onContinue()
    }

    override fun exit(
        feature: FeatureKey,
        reason: FeatureExitReason,
        onContinue: () -> Unit,
    ) {
        onOperationBusyChanged(false)
        onContinue()
    }
}

private fun FeatureOperationEvent.operationBusyState(): Boolean =
    when (this) {
        is FeatureOperationEvent.ScanStarted,
        is FeatureOperationEvent.OperationStarted -> true
        is FeatureOperationEvent.ScanFinished,
        is FeatureOperationEvent.OperationFinished -> false
    }

private fun FeatureOperationEvent.completedCleanup(): Boolean =
    when (this) {
        is FeatureOperationEvent.ScanFinished -> !hasResult && feature in cleanupFeatures
        is FeatureOperationEvent.OperationFinished ->
            success &&
                feature in cleanupFeatures &&
                action in cleanupActions
        else -> false
    }

private val cleanupActions =
    setOf(OperationAction.CLEAN, OperationAction.DELETE, OperationAction.REMOVE_LOCATION)

private val cleanupFeatures =
    FeatureKey.entries.filterTo(mutableSetOf()) { it.group == FeatureGroup.FILES } +
        setOf(
            FeatureKey.JUNK_CLEAN,
            FeatureKey.ANTI_VIRUS,
            FeatureKey.NOTIFICATION_CLEANER,
            FeatureKey.WHATSAPP_CLEANER,
        )
