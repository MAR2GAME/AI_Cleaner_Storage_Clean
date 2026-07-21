package com.aicleanerstorage.spaceclean.feature.startup

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.runtime.startup.AppLaunchRequest
import com.aicleanerstorage.spaceclean.app.runtime.startup.NotificationLaunchSource
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplashViewModelTest {
    @Test
    fun `normal launch enters finishing after request and visual readiness`() {
        var state = SplashMachineState()

        state = transition(state, SplashAction.LaunchRequestChanged(AppLaunchRequest.Normal))
        assertEquals(SplashStage.Preparing, state.stage)

        state = transition(state, SplashAction.VisualReady)
        assertEquals(SplashStage.Finishing, state.stage)
        assertEquals(SplashProgressStage.Finishing, state.toUiState().progressStage)
    }

    @Test
    fun `initial notification launch opens target after visual completion`() {
        var state = SplashMachineState(visualReady = true)
        val request = notificationRequest(NotificationLaunchSource.InitialIntent)

        state = transition(state, SplashAction.LaunchRequestChanged(request))
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
        assertTrue(state.toUiState().paused)

        state = transition(state, SplashAction.PermissionPauseChanged(false))
        assertTrue(state.toUiState().paused)

        state = transition(state, SplashAction.ExternalLinkStateChanged(false))
        assertFalse(state.toUiState().paused)
    }

    @Test
    fun `view model navigates to onboarding for a fresh install`() = runTest {
        val viewModel = SplashViewModel(FakeOnboardingPreferences())

        viewModel.onAction(SplashAction.LaunchRequestChanged(AppLaunchRequest.Normal))
        viewModel.onAction(SplashAction.VisualReady)
        viewModel.onAction(SplashAction.VisualFinished)

        assertEquals(SplashEffect.Navigate(AppDestination.OnboardingScan), viewModel.effects.first())
    }

    @Test
    fun `progress stages use fixed targets`() {
        assertEquals(0.25f, SplashProgressStage.Preparing.targetProgress, 0f)
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
