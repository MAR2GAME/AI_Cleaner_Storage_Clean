package com.aicleanerstorage.spaceclean.feature.startup

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchRequest
import com.aicleanerstorage.spaceclean.app.startup.NotificationLaunchSource

internal data class SplashMachineState(
    val stage: SplashStage = SplashStage.Preparing,
    val visualReady: Boolean = false,
    val launchRequest: AppLaunchRequest? = null,
    val pauseReasons: Set<SplashPauseReason> = emptySet(),
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
        is SplashAction.ExternalLinkStateChanged ->
            SplashTransition(state.withPauseReason(SplashPauseReason.ExternalLink, action.active))
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
    return SplashTransition(state.copy(stage = SplashStage.Finishing))
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
            when (stage) {
                SplashStage.Preparing -> SplashProgressStage.Preparing
                SplashStage.Finishing -> SplashProgressStage.Finishing
                SplashStage.Completed -> SplashProgressStage.Completed
            },
    )
