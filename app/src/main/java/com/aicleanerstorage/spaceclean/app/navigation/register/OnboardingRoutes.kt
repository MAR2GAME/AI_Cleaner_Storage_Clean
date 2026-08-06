package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.ads.AdScene
import com.aicleanerstorage.spaceclean.common.ads.InterstitialAdRunner
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingRoute
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingScanViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerOnboardingRoute(
    navigator: AppNavigator,
    interstitialAds: InterstitialAdRunner,
    analyticsReporter: AnalyticsReporter,
) {
    composable(AppDestination.OnboardingScan.route) {
        val viewModel: OnboardingScanViewModel = koinViewModel()
        OnboardingRoute(
            navigator = navigator,
            analyticsReporter = analyticsReporter,
            onScanFinishedAd = { onContinue ->
                interstitialAds.run(AdScene.OnboardingScanFinished, "onboarding_scan_finish", onContinue)
            },
            onCompletionAd = { skipped, onContinue ->
                interstitialAds.run(
                    scene = AdScene.OnboardingSkipped,
                    requestId = if (skipped) "onboarding_skip" else "onboarding_get_started",
                    onContinue = onContinue,
                )
            },
            viewModel = viewModel,
        )
    }
}
