package com.aicleanerstorage.spaceclean.app.view.onboarding.ui

import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingEvent
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingAdTest {
    @Test
    fun scanAndGetStartedRequestLegacyPlacementsBeforeHome() {
        val requests = mutableListOf<String>()
        val destinations = mutableListOf<AppDestination>()
        var scanFinished = false
        lateinit var continueAfterAd: () -> Unit
        val showAd: (String, String, () -> Unit) -> Unit = { area, _, onContinue ->
            requests.add(area)
            continueAfterAd = onContinue
        }

        OnboardingEvent.NotifyScanFinished.requestOnboardingAd(showAd, { scanFinished = true }, destinations::add)
        assertEquals(listOf("newGuideScanFinishAdv"), requests)
        continueAfterAd()
        assertEquals(true, scanFinished)
        assertEquals(emptyList<AppDestination>(), destinations)

        OnboardingEvent
            .FinishOnboarding(AppDestination.Main(), skipped = false)
            .requestOnboardingAd(
                showAd,
                { error("Unexpected scan callback") },
                destinations::add,
            )
        assertEquals(listOf("newGuideScanFinishAdv", "newGuideSkipAdv"), requests)
        assertEquals(emptyList<AppDestination>(), destinations)
        continueAfterAd()
        assertEquals(listOf(AppDestination.Main()), destinations)
    }

    @Test
    fun skipUsesTheSameCompletionPlacement() {
        val requests = mutableListOf<String>()
        val destinations = mutableListOf<AppDestination>()
        lateinit var continueAfterAd: () -> Unit
        val showAd: (String, String, () -> Unit) -> Unit = { area, _, onContinue ->
            requests.add(area)
            continueAfterAd = onContinue
        }

        OnboardingEvent
            .FinishOnboarding(AppDestination.Main(), skipped = true)
            .requestOnboardingAd(showAd, { error("Unexpected scan callback") }, destinations::add)
        assertEquals(listOf("newGuideSkipAdv"), requests)

        continueAfterAd()
        assertEquals(listOf(AppDestination.Main()), destinations)
    }
}
