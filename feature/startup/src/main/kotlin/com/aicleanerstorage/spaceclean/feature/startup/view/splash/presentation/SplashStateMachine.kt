package com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.startup.AppLaunchRequest
import com.aicleanerstorage.spaceclean.core.contracts.startup.NotificationLaunchSource

data class SplashUiState(
    val stage: SplashStage = SplashStage.Preparing,
    val visualReady: Boolean = false,
    val launchRequest: AppLaunchRequest? = null,
    val pauseReasons: Set<SplashPauseReason> = emptySet(),
    val openAdLoaded: Boolean = false,
) {
    val paused: Boolean get() = pauseReasons.isNotEmpty()
    val finishRequested: Boolean get() = stage == SplashStage.Finishing
    val progressStage: SplashProgressStage get() = when {
        stage == SplashStage.Preparing -> SplashProgressStage.Preparing
        stage == SplashStage.WaitingForOpenAd && openAdLoaded -> SplashProgressStage.ShowingAd
        stage == SplashStage.WaitingForOpenAd -> SplashProgressStage.RequestingAd
        stage == SplashStage.Finishing -> SplashProgressStage.Finishing
        else -> SplashProgressStage.Completed
    }
}

data class SplashTransition(
    val state: SplashUiState,
    val effects: List<SplashEffect> = emptyList(),
)

fun reduceSplashState(
    state: SplashUiState,
    action: SplashIntent,
    normalDestination: AppDestination,
): SplashTransition {
    if (state.stage == SplashStage.Completed) return SplashTransition(state)

    return when (action) {
        is SplashIntent.LaunchRequestChanged -> {
            val request = action.request
            if (request is AppLaunchRequest.NotificationTarget &&
                request.source == NotificationLaunchSource.NewIntent
            ) {
                SplashTransition(
                    state = state.copy(stage = SplashStage.Completed, launchRequest = request),
                    effects = listOf(SplashEffect.OpenNotificationTarget(AppDestination.forRoute(request.route) ?: AppDestination.Main())),
                )
            } else {
                advancePreparing(state.copy(launchRequest = request))
            }
        }
        SplashIntent.VisualReady -> advancePreparing(state.copy(visualReady = true))
        SplashIntent.VisualFinished -> finishVisual(state, normalDestination)
        is SplashIntent.PermissionPauseChanged ->
            SplashTransition(state.withPauseReason(SplashPauseReason.Permission, action.active))
        is SplashIntent.OpenAdStateChanged ->
            SplashTransition(
                state
                    .copy(openAdLoaded = state.openAdLoaded || action.active)
                    .withPauseReason(SplashPauseReason.OpenAd, action.active),
            )
        is SplashIntent.ExternalLinkStateChanged ->
            SplashTransition(state.withPauseReason(SplashPauseReason.ExternalLink, action.active))
        SplashIntent.OpenAdFinished -> {
            val resumed = state.withPauseReason(SplashPauseReason.OpenAd, active = false)
            SplashTransition(
                if (resumed.stage == SplashStage.WaitingForOpenAd) {
                    resumed.copy(stage = SplashStage.Finishing)
                } else {
                    resumed
                },
            )
        }
    }
}

private fun finishVisual(
    state: SplashUiState,
    normalDestination: AppDestination,
): SplashTransition {
    if (state.stage != SplashStage.Finishing) return SplashTransition(state)
    val effect =
        when (val request = state.launchRequest) {
            is AppLaunchRequest.NotificationTarget -> SplashEffect.OpenNotificationTarget(AppDestination.forRoute(request.route) ?: AppDestination.Main())
            AppLaunchRequest.Normal -> SplashEffect.Navigate(normalDestination)
            null -> return SplashTransition(state)
        }
    return SplashTransition(
        state = state.copy(stage = SplashStage.Completed),
        effects = listOf(effect),
    )
}

private fun advancePreparing(state: SplashUiState): SplashTransition {
    if (state.stage != SplashStage.Preparing || !state.visualReady || state.launchRequest == null) {
        return SplashTransition(state)
    }
    return when (state.launchRequest) {
        AppLaunchRequest.Normal ->
            SplashTransition(
                state =
                    state
                        .copy(stage = SplashStage.WaitingForOpenAd)
                        .withPauseReason(SplashPauseReason.OpenAd, active = true),
                effects = listOf(SplashEffect.RunColdStartAd),
            )
        is AppLaunchRequest.NotificationTarget ->
            SplashTransition(state.copy(stage = SplashStage.Finishing))
    }
}

private fun SplashUiState.withPauseReason(
    reason: SplashPauseReason,
    active: Boolean,
): SplashUiState =
    copy(
        pauseReasons =
            if (active) {
                pauseReasons + reason
            } else {
                pauseReasons - reason
            },
    )

