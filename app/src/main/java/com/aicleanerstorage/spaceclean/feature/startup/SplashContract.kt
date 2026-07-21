package com.aicleanerstorage.spaceclean.feature.startup

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.runtime.startup.AppLaunchRequest

enum class SplashStage {
    Preparing,
    Finishing,
    Completed,
}

enum class SplashPauseReason {
    Permission,
    ExternalLink,
}

enum class SplashProgressStage {
    Preparing,
    Finishing,
    Completed,
}

internal val SplashProgressStage.targetProgress: Float
    get() =
        when (this) {
            SplashProgressStage.Preparing -> 0.25f
            SplashProgressStage.Finishing,
            SplashProgressStage.Completed,
            -> 1f
        }

data class SplashUiState(
    val stage: SplashStage = SplashStage.Preparing,
    val paused: Boolean = false,
    val progressStage: SplashProgressStage = SplashProgressStage.Preparing,
) {
    val finishRequested: Boolean
        get() = stage == SplashStage.Finishing
}

sealed interface SplashAction {
    data class LaunchRequestChanged(val request: AppLaunchRequest) : SplashAction

    data object VisualReady : SplashAction

    data object VisualFinished : SplashAction

    data class PermissionPauseChanged(val active: Boolean) : SplashAction

    data class ExternalLinkStateChanged(val active: Boolean) : SplashAction
}

sealed interface SplashEffect {
    data class OpenNotificationTarget(val route: String) : SplashEffect

    data class Navigate(val destination: AppDestination) : SplashEffect
}
