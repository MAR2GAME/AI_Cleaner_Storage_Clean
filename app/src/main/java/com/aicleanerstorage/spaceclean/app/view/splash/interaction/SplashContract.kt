package com.aicleanerstorage.spaceclean.app.view.splash.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

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
    RequestingAd,
    ShowingAd,
    Finishing,
    Completed,
}

val SplashProgressStage.targetProgress: Float
    get() =
        when (this) {
            SplashProgressStage.Preparing -> 0.25f
            SplashProgressStage.RequestingAd,
            SplashProgressStage.ShowingAd,
            -> 0.25f
            SplashProgressStage.Finishing,
            SplashProgressStage.Completed,
            -> 1f
        }

sealed interface SplashAction {
    data object VisualPreparationCompleted : SplashAction

    data object VisualFinished : SplashAction

    data class PermissionPauseChanged(val active: Boolean) : SplashAction

    data class ExternalLinkStateChanged(val active: Boolean) : SplashAction
}

sealed interface SplashEvent {
    data class Navigate(val destination: AppDestination) : SplashEvent
}
