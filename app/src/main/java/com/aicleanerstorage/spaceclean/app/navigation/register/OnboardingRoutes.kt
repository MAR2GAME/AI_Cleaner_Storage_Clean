package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.feature.onboarding.view.scan.ui.OnboardingRoute
import com.aicleanerstorage.spaceclean.feature.onboarding.view.scan.presentation.OnboardingScanViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerOnboardingRoute(
    navigator: NavHostAppNavigator,
    onScanFinished: suspend () -> Unit,
    onCompletion: suspend (Boolean) -> Unit,
    analyticsReporter: AnalyticsReporter,
) {
    composable<AppDestination.OnboardingScan> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: OnboardingScanViewModel = koinViewModel()
        OnboardingRoute(
            navigator = pageNavigator,
            analyticsReporter = analyticsReporter,
            onScanFinishedAd = onScanFinished,
            onCompletionAd = onCompletion,
            viewModel = viewModel,
        )
    }
}
