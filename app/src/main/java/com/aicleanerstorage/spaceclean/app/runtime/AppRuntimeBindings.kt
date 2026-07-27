package com.aicleanerstorage.spaceclean.app.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore

internal data class AppRuntimeBindings(
    val featureFlow: FeatureFlow,
    val externalActivities: ExternalActivityTracker,
)

@Composable
internal fun rememberAppRuntimeBindings(
    sessionCoordinator: AppSessionCoordinator,
    cleanupHistory: CleanupHistoryStore,
): AppRuntimeBindings {
    val externalActivityTracker = remember { ExternalActivityTracker() }
    val featureFlow =
        remember(sessionCoordinator, cleanupHistory) {
            LocalFeatureFlow(
                cleanupHistory = cleanupHistory,
                onOperationBusyChanged = { active ->
                    sessionCoordinator.set(RuntimeBusyReason.FeatureOperation, active)
                },
            )
        }
    return remember(featureFlow, externalActivityTracker) {
        AppRuntimeBindings(
            featureFlow = featureFlow,
            externalActivities = externalActivityTracker,
        )
    }
}
