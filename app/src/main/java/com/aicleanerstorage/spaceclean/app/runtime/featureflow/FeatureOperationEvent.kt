package com.aicleanerstorage.spaceclean.app.runtime.featureflow

import com.aicleanerstorage.spaceclean.app.navigation.feature.FeatureKey

sealed interface FeatureOperationEvent {
    val feature: FeatureKey

    data class ScanStarted(
        override val feature: FeatureKey,
    ) : FeatureOperationEvent

    data class ScanFinished(
        override val feature: FeatureKey,
        val hasResult: Boolean,
    ) : FeatureOperationEvent

    data class OperationStarted(
        override val feature: FeatureKey,
        val action: OperationAction,
    ) : FeatureOperationEvent

    data class OperationFinished(
        override val feature: FeatureKey,
        val action: OperationAction,
        val success: Boolean,
    ) : FeatureOperationEvent
}

enum class FeatureExitReason {
    Return,
    PermissionRejected,
}

enum class OperationAction {
    CLEAN,
    DELETE,
    REMOVE_LOCATION,
    TEST,
}
