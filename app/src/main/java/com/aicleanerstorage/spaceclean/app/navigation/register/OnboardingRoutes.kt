package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.ads.AdScene
import com.aicleanerstorage.spaceclean.core.contracts.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.feature.onboarding.view.scan.ui.OnboardingRoute
import com.aicleanerstorage.spaceclean.feature.onboarding.view.scan.presentation.OnboardingScanViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerOnboardingRoute(
    navigator: NavHostAppNavigator,
    showAd: suspend (InterstitialRequest) -> Unit,
    analyticsReporter: AnalyticsReporter,
) {
    composable<AppDestination.OnboardingScan> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: OnboardingScanViewModel = koinViewModel()
        OnboardingRoute(
            navigator = pageNavigator,
            analyticsReporter = analyticsReporter,
            onScanFinishedAd = {
                showAd(InterstitialRequest(AdScene.OnboardingScanFinished, "onboarding_scan_finish"))
            },
            onCompletionAd = { skipped ->
                showAd(InterstitialRequest(
                    scene = AdScene.OnboardingSkipped,
                    requestId = if (skipped) "onboarding_skip" else "onboarding_get_started",
                ))
            },
            viewModel = viewModel,
        )
    }
}
