package com.aicleanerstorage.spaceclean.app.view.splash.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplashReducerTest {
    private val destination = AppDestination.Main()

    @Test
    fun `preparation moves to finishing only once`() {
        val first =
            SplashReducer.reduce(SplashUiState(), SplashAction.VisualPreparationCompleted, destination)

        assertEquals(SplashStage.Finishing, first.state.stage)
        assertTrue(first.events.isEmpty())

        val second =
            SplashReducer.reduce(first.state, SplashAction.VisualPreparationCompleted, destination)

        assertEquals(first.state, second.state)
        assertTrue(second.events.isEmpty())
    }

    @Test
    fun `finish completes the flow and emits navigate`() {
        val transition =
            SplashReducer.reduce(
                SplashUiState(stage = SplashStage.Finishing),
                SplashAction.VisualFinished,
                destination,
            )

        assertEquals(SplashStage.Completed, transition.state.stage)
        assertEquals(listOf(SplashEvent.Navigate(destination)), transition.events)
    }

    @Test
    fun `finish before preparation is ignored`() {
        val transition = SplashReducer.reduce(SplashUiState(), SplashAction.VisualFinished, destination)

        assertEquals(SplashUiState(), transition.state)
        assertTrue(transition.events.isEmpty())
    }

    @Test
    fun `completed ignores every action`() {
        val completed = SplashUiState(stage = SplashStage.Completed)
        val actions =
            listOf(
                SplashAction.VisualPreparationCompleted,
                SplashAction.VisualFinished,
                SplashAction.TermsClicked,
                SplashAction.PrivacyClicked,
                SplashAction.PermissionPauseChanged(true),
                SplashAction.ExternalLinkStateChanged(true),
            )

        actions.forEach { action ->
            val transition = SplashReducer.reduce(completed, action, destination)
            assertEquals(completed, transition.state)
            assertTrue(transition.events.isEmpty())
        }
    }

    @Test
    fun `permission pause toggles without changing the stage`() {
        val paused =
            SplashReducer.reduce(SplashUiState(), SplashAction.PermissionPauseChanged(true), destination)

        assertTrue(paused.state.paused)
        assertEquals(SplashStage.Preparing, paused.state.stage)

        val resumed =
            SplashReducer.reduce(paused.state, SplashAction.PermissionPauseChanged(false), destination)

        assertFalse(resumed.state.paused)
    }

    @Test
    fun `legal clicks pause and emit open events`() {
        val terms = SplashReducer.reduce(SplashUiState(), SplashAction.TermsClicked, destination)

        assertEquals(setOf(SplashPauseReason.ExternalLink), terms.state.pauseReasons)
        assertEquals(listOf(SplashEvent.OpenTerms), terms.events)

        val privacy = SplashReducer.reduce(SplashUiState(), SplashAction.PrivacyClicked, destination)

        assertEquals(setOf(SplashPauseReason.ExternalLink), privacy.state.pauseReasons)
        assertEquals(listOf(SplashEvent.OpenPrivacy), privacy.events)
    }

    @Test
    fun `external link state clears the pause`() {
        val paused = SplashUiState(pauseReasons = setOf(SplashPauseReason.ExternalLink))

        val cleared =
            SplashReducer.reduce(paused, SplashAction.ExternalLinkStateChanged(false), destination)

        assertFalse(cleared.state.paused)
    }
}
