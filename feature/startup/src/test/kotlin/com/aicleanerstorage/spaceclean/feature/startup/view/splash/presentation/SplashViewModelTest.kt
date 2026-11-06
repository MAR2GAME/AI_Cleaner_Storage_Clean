package com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation

import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashUiState
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashStage
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashPauseReason
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashProgressStage
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.targetProgress
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashIntent
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashEffect
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.reduceSplashState
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashViewModel

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.startup.AppLaunchRequest
import com.aicleanerstorage.spaceclean.core.contracts.startup.NotificationLaunchSource
import com.aicleanerstorage.spaceclean.core.contracts.onboarding.OnboardingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplashViewModelTest {
    @Test
    fun `normal launch waits for open ad after request and visual readiness`() {
        var state = SplashUiState()

        state = transition(state, SplashIntent.LaunchRequestChanged(AppLaunchRequest.Normal))
        assertEquals(SplashStage.Preparing, state.stage)

        val result = reduceSplashState(state, SplashIntent.VisualReady, AppDestination.Main())

        assertEquals(SplashStage.WaitingForOpenAd, result.state.stage)
        assertEquals(SplashProgressStage.RequestingAd, result.state.progressStage)
        assertTrue(result.state.paused)
        assertEquals(listOf(SplashEffect.RunColdStartAd), result.effects)
    }

    @Test
    fun `open ad active pauses splash and reports showing ad stage`() {
        val state =
            SplashUiState(
                stage = SplashStage.WaitingForOpenAd,
                launchRequest = AppLaunchRequest.Normal,
                pauseReasons = setOf(SplashPauseReason.OpenAd),
            )

        val loaded = transition(state, SplashIntent.OpenAdStateChanged(true))

        assertTrue(loaded.paused)
        assertEquals(SplashProgressStage.ShowingAd, loaded.progressStage)
    }

    @Test
    fun `open ad inactive clears pause without navigating`() {
        val state =
            SplashUiState(
                stage = SplashStage.WaitingForOpenAd,
                launchRequest = AppLaunchRequest.Normal,
                pauseReasons = setOf(SplashPauseReason.OpenAd),
                openAdLoaded = true,
            )

        val result = reduceSplashState(state, SplashIntent.OpenAdStateChanged(false), AppDestination.Main())

        assertEquals(SplashStage.WaitingForOpenAd, result.state.stage)
        assertFalse(result.state.paused)
        assertEquals(emptyList<SplashEffect>(), result.effects)
    }

    @Test
    fun `open ad finished advances to finishing`() {
        val state =
            SplashUiState(
                stage = SplashStage.WaitingForOpenAd,
                launchRequest = AppLaunchRequest.Normal,
                pauseReasons = setOf(SplashPauseReason.OpenAd),
            )

        val finished = transition(state, SplashIntent.OpenAdFinished)

        assertEquals(SplashStage.Finishing, finished.stage)
        assertEquals(SplashProgressStage.Finishing, finished.progressStage)
    }

    @Test
    fun `initial notification launch opens target after visual completion`() {
        var state = SplashUiState(visualReady = true)
        val request = notificationRequest(NotificationLaunchSource.InitialIntent)

        val launchResult = reduceSplashState(state, SplashIntent.LaunchRequestChanged(request), AppDestination.Main())
        state = launchResult.state

        assertEquals(SplashStage.Finishing, state.stage)
        assertEquals(emptyList<SplashEffect>(), launchResult.effects)

        val result = reduceSplashState(state, SplashIntent.VisualFinished, AppDestination.Main())

        assertEquals(SplashStage.Completed, result.state.stage)
        assertEquals(listOf(SplashEffect.OpenNotificationTarget(AppDestination.forRoute(request.route) ?: AppDestination.Main())), result.effects)
    }

    @Test
    fun `new intent notification bypasses the remaining splash animation`() {
        val request = notificationRequest(NotificationLaunchSource.NewIntent)

        val result =
            reduceSplashState(
                SplashUiState(),
                SplashIntent.LaunchRequestChanged(request),
                AppDestination.Main(),
            )

        assertEquals(SplashStage.Completed, result.state.stage)
        assertEquals(listOf(SplashEffect.OpenNotificationTarget(AppDestination.forRoute(request.route) ?: AppDestination.Main())), result.effects)
    }

    @Test
    fun `permission and legal pauses are tracked independently`() {
        var state = SplashUiState()
        state = transition(state, SplashIntent.PermissionPauseChanged(true))
        state = transition(state, SplashIntent.ExternalLinkStateChanged(true))
        state = transition(state, SplashIntent.OpenAdStateChanged(true))
        assertTrue(state.paused)

        state = transition(state, SplashIntent.PermissionPauseChanged(false))
        assertTrue(state.paused)

        state = transition(state, SplashIntent.ExternalLinkStateChanged(false))
        assertTrue(state.paused)

        state = transition(state, SplashIntent.OpenAdStateChanged(false))
        assertFalse(state.paused)
    }

    @Test
    fun `view model navigates to onboarding for a fresh install`() = runTest {
        val viewModel = SplashViewModel(FakeOnboardingPreferences())

        viewModel.dispatch(SplashIntent.LaunchRequestChanged(AppLaunchRequest.Normal))
        viewModel.dispatch(SplashIntent.VisualReady)
        assertEquals(SplashEffect.RunColdStartAd, viewModel.effects.first())

        viewModel.dispatch(SplashIntent.OpenAdFinished)
        viewModel.dispatch(SplashIntent.VisualFinished)

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

    private fun transition(state: SplashUiState, action: SplashIntent): SplashUiState =
        reduceSplashState(state, action, AppDestination.Main()).state

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
