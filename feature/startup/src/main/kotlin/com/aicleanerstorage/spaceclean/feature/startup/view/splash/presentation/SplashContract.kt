package com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.startup.AppLaunchRequest

enum class SplashStage {
    Preparing,
    WaitingForOpenAd,
    Finishing,
    Completed,
}

enum class SplashPauseReason {
    Permission,
    OpenAd,
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

sealed interface SplashIntent {
    data class LaunchRequestChanged(val request: AppLaunchRequest) : SplashIntent

    data object VisualReady : SplashIntent

    data object VisualFinished : SplashIntent

    data class PermissionPauseChanged(val active: Boolean) : SplashIntent

    data class OpenAdStateChanged(val active: Boolean) : SplashIntent

    data class ExternalLinkStateChanged(val active: Boolean) : SplashIntent

    data object OpenAdFinished : SplashIntent
}

sealed interface SplashEffect {
    data object RunColdStartAd : SplashEffect

    data class OpenNotificationTarget(val destination: AppDestination) : SplashEffect

    data class Navigate(val destination: AppDestination) : SplashEffect
}
