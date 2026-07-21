package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingRoute
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingScanViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerOnboardingRoute(
    navigator: AppNavigator,
) {
    composable(AppDestination.OnboardingScan.route) {
        val viewModel: OnboardingScanViewModel = koinViewModel()
        OnboardingRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }
}
