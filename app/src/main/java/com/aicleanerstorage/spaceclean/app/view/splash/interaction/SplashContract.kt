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

data class SplashUiState(
    val stage: SplashStage = SplashStage.Preparing,
    val pauseReasons: Set<SplashPauseReason> = emptySet(),
) {
    val paused: Boolean get() = pauseReasons.isNotEmpty()
    val finishRequested: Boolean get() = stage == SplashStage.Finishing
}

sealed interface SplashAction {
    data object VisualPreparationCompleted : SplashAction

    data object VisualFinished : SplashAction

    data object TermsClicked : SplashAction

    data object PrivacyClicked : SplashAction

    data class PermissionPauseChanged(
        val active: Boolean,
    ) : SplashAction

    data class ExternalLinkStateChanged(
        val active: Boolean,
    ) : SplashAction
}

sealed interface SplashEvent {
    data class Navigate(
        val destination: AppDestination,
    ) : SplashEvent

    data object OpenTerms : SplashEvent

    data object OpenPrivacy : SplashEvent
}
