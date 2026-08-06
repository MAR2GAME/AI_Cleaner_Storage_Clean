package com.aicleanerstorage.spaceclean.feature.startup

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchRequest
import com.aicleanerstorage.spaceclean.app.startup.NotificationLaunchSource

internal data class SplashMachineState(
    val stage: SplashStage = SplashStage.Preparing,
    val visualReady: Boolean = false,
    val launchRequest: AppLaunchRequest? = null,
    val pauseReasons: Set<SplashPauseReason> = emptySet(),
    val openAdLoaded: Boolean = false,
)

internal data class SplashTransition(
    val state: SplashMachineState,
    val effects: List<SplashEffect> = emptyList(),
)

internal fun reduceSplashState(
    state: SplashMachineState,
    action: SplashAction,
    normalDestination: AppDestination,
): SplashTransition {
    if (state.stage == SplashStage.Completed) return SplashTransition(state)

    return when (action) {
        is SplashAction.LaunchRequestChanged -> {
            val request = action.request
            if (request is AppLaunchRequest.NotificationTarget &&
                request.source == NotificationLaunchSource.NewIntent
            ) {
                SplashTransition(
                    state = state.copy(stage = SplashStage.Completed, launchRequest = request),
                    effects = listOf(SplashEffect.OpenNotificationTarget(request.route)),
                )
            } else {
                advancePreparing(state.copy(launchRequest = request))
            }
        }
        SplashAction.VisualReady -> advancePreparing(state.copy(visualReady = true))
        SplashAction.VisualFinished -> finishVisual(state, normalDestination)
        is SplashAction.PermissionPauseChanged ->
            SplashTransition(state.withPauseReason(SplashPauseReason.Permission, action.active))
        is SplashAction.OpenAdStateChanged ->
            SplashTransition(
                state
                    .copy(openAdLoaded = state.openAdLoaded || action.active)
                    .withPauseReason(SplashPauseReason.OpenAd, action.active),
            )
        is SplashAction.ExternalLinkStateChanged ->
            SplashTransition(state.withPauseReason(SplashPauseReason.ExternalLink, action.active))
        SplashAction.OpenAdFinished -> {
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
    state: SplashMachineState,
    normalDestination: AppDestination,
): SplashTransition {
    if (state.stage != SplashStage.Finishing) return SplashTransition(state)
    val effect =
        when (val request = state.launchRequest) {
            is AppLaunchRequest.NotificationTarget -> SplashEffect.OpenNotificationTarget(request.route)
            AppLaunchRequest.Normal -> SplashEffect.Navigate(normalDestination)
            null -> return SplashTransition(state)
        }
    return SplashTransition(
        state = state.copy(stage = SplashStage.Completed),
        effects = listOf(effect),
    )
}

private fun advancePreparing(state: SplashMachineState): SplashTransition {
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

private fun SplashMachineState.withPauseReason(
    reason: SplashPauseReason,
    active: Boolean,
): SplashMachineState =
    copy(
        pauseReasons =
            if (active) {
                pauseReasons + reason
            } else {
                pauseReasons - reason
            },
    )

internal fun SplashMachineState.toUiState(): SplashUiState =
    SplashUiState(
        stage = stage,
        paused = pauseReasons.isNotEmpty(),
        progressStage =
            when {
                stage == SplashStage.Preparing -> SplashProgressStage.Preparing
                stage == SplashStage.WaitingForOpenAd && openAdLoaded -> SplashProgressStage.ShowingAd
                stage == SplashStage.WaitingForOpenAd -> SplashProgressStage.RequestingAd
                stage == SplashStage.Finishing -> SplashProgressStage.Finishing
                else -> SplashProgressStage.Completed
            },
    )
