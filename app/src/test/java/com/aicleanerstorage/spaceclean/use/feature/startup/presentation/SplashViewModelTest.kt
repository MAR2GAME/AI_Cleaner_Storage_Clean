package com.aicleanerstorage.spaceclean.feature.startup

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchRequest
import com.aicleanerstorage.spaceclean.app.startup.NotificationLaunchSource
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplashViewModelTest {
    @Test
    fun `normal launch waits for open ad after request and visual readiness`() {
        var state = SplashMachineState()

        state = transition(state, SplashAction.LaunchRequestChanged(AppLaunchRequest.Normal))
        assertEquals(SplashStage.Preparing, state.stage)

        val result = reduceSplashState(state, SplashAction.VisualReady, AppDestination.Home)

        assertEquals(SplashStage.WaitingForOpenAd, result.state.stage)
        assertEquals(SplashProgressStage.RequestingAd, result.state.toUiState().progressStage)
        assertTrue(result.state.toUiState().paused)
        assertEquals(listOf(SplashEffect.RunColdStartAd), result.effects)
    }

    @Test
    fun `open ad active pauses splash and reports showing ad stage`() {
        val state =
            SplashMachineState(
                stage = SplashStage.WaitingForOpenAd,
                launchRequest = AppLaunchRequest.Normal,
                pauseReasons = setOf(SplashPauseReason.OpenAd),
            )

        val loaded = transition(state, SplashAction.OpenAdStateChanged(true))

        assertTrue(loaded.toUiState().paused)
        assertEquals(SplashProgressStage.ShowingAd, loaded.toUiState().progressStage)
    }

    @Test
    fun `open ad inactive clears pause without navigating`() {
        val state =
            SplashMachineState(
                stage = SplashStage.WaitingForOpenAd,
                launchRequest = AppLaunchRequest.Normal,
                pauseReasons = setOf(SplashPauseReason.OpenAd),
                openAdLoaded = true,
            )

        val result = reduceSplashState(state, SplashAction.OpenAdStateChanged(false), AppDestination.Home)

        assertEquals(SplashStage.WaitingForOpenAd, result.state.stage)
        assertFalse(result.state.toUiState().paused)
        assertEquals(emptyList<SplashEffect>(), result.effects)
    }

    @Test
    fun `open ad finished advances to finishing`() {
        val state =
            SplashMachineState(
                stage = SplashStage.WaitingForOpenAd,
                launchRequest = AppLaunchRequest.Normal,
                pauseReasons = setOf(SplashPauseReason.OpenAd),
            )

        val finished = transition(state, SplashAction.OpenAdFinished)

        assertEquals(SplashStage.Finishing, finished.stage)
        assertEquals(SplashProgressStage.Finishing, finished.toUiState().progressStage)
    }

    @Test
    fun `initial notification launch opens target after visual completion`() {
        var state = SplashMachineState(visualReady = true)
        val request = notificationRequest(NotificationLaunchSource.InitialIntent)

        val launchResult = reduceSplashState(state, SplashAction.LaunchRequestChanged(request), AppDestination.Home)
        state = launchResult.state

        assertEquals(SplashStage.Finishing, state.stage)
        assertEquals(emptyList<SplashEffect>(), launchResult.effects)

        val result = reduceSplashState(state, SplashAction.VisualFinished, AppDestination.Home)

        assertEquals(SplashStage.Completed, result.state.stage)
        assertEquals(listOf(SplashEffect.OpenNotificationTarget(request.route)), result.effects)
    }

    @Test
    fun `new intent notification bypasses the remaining splash animation`() {
        val request = notificationRequest(NotificationLaunchSource.NewIntent)

        val result =
            reduceSplashState(
                SplashMachineState(),
                SplashAction.LaunchRequestChanged(request),
                AppDestination.Home,
            )

        assertEquals(SplashStage.Completed, result.state.stage)
        assertEquals(listOf(SplashEffect.OpenNotificationTarget(request.route)), result.effects)
    }

    @Test
    fun `permission and legal pauses are tracked independently`() {
        var state = SplashMachineState()
        state = transition(state, SplashAction.PermissionPauseChanged(true))
        state = transition(state, SplashAction.ExternalLinkStateChanged(true))
        state = transition(state, SplashAction.OpenAdStateChanged(true))
        assertTrue(state.toUiState().paused)

        state = transition(state, SplashAction.PermissionPauseChanged(false))
        assertTrue(state.toUiState().paused)

        state = transition(state, SplashAction.ExternalLinkStateChanged(false))
        assertTrue(state.toUiState().paused)

        state = transition(state, SplashAction.OpenAdStateChanged(false))
        assertFalse(state.toUiState().paused)
    }

    @Test
    fun `view model navigates to onboarding for a fresh install`() = runTest {
        val viewModel = SplashViewModel(FakeOnboardingPreferences())

        viewModel.onAction(SplashAction.LaunchRequestChanged(AppLaunchRequest.Normal))
        viewModel.onAction(SplashAction.VisualReady)
        assertEquals(SplashEffect.RunColdStartAd, viewModel.effects.first())

        viewModel.onAction(SplashAction.OpenAdFinished)
        viewModel.onAction(SplashAction.VisualFinished)

        assertEquals(SplashEffect.Navigate(AppDestination.OnboardingScan), viewModel.effects.first())
    }

    @Test
    fun `progress stages use fixed targets`() {
        assertEquals(0.25f, SplashProgressStage.Preparing.targetProgress, 0f)
        assertEquals(0.25f, SplashProgressStage.RequestingAd.targetProgress, 0f)
        assertEquals(0.25f, SplashProgressStage.ShowingAd.targetProgress, 0f)
        assertEquals(1f, SplashProgressStage.Finishing.targetProgress, 0f)
        assertEquals(1f, SplashProgressStage.Completed.targetProgress, 0f)
    }

    private fun transition(state: SplashMachineState, action: SplashAction): SplashMachineState =
        reduceSplashState(state, action, AppDestination.Home).state

    private fun notificationRequest(source: NotificationLaunchSource): AppLaunchRequest.NotificationTarget =
        AppLaunchRequest.NotificationTarget(
            route = AppDestination.NotificationCleaner.route,
            source = source,
        )

    private class FakeOnboardingPreferences : OnboardingPreferences {
        override fun isScanCompleted(): Boolean = false

        override fun markScanCompleted() = Unit
    }
}
