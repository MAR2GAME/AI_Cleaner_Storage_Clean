package com.aicleanerstorage.spaceclean.app.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.aicleanerstorage.spaceclean.app.runtime.external.ExternalActivityLauncher
import com.aicleanerstorage.spaceclean.app.runtime.featureflow.DefaultFeatureFlowRuntime
import com.aicleanerstorage.spaceclean.app.runtime.featureflow.FeatureFlowRuntime
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore

internal data class AppRuntimeBindings(
    val featureFlow: FeatureFlowRuntime,
    val externalActivities: ExternalActivityLauncher,
)

@Composable
internal fun rememberAppRuntimeBindings(
    sessionCoordinator: AppSessionCoordinator,
    cleanupHistory: CleanupHistoryStore,
): AppRuntimeBindings {
    val externalActivityLauncher = remember { ExternalActivityLauncher() }
    val featureFlow =
        remember(sessionCoordinator, cleanupHistory) {
            DefaultFeatureFlowRuntime(
                cleanupHistory = cleanupHistory,
                onOperationBusyChanged = { active ->
                    sessionCoordinator.set(RuntimeBusyReason.FeatureOperation, active)
                },
            )
        }
    return remember(featureFlow, externalActivityLauncher) {
        AppRuntimeBindings(
            featureFlow = featureFlow,
            externalActivities = externalActivityLauncher,
        )
    }
}
