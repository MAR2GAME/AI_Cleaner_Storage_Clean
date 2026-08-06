package com.aicleanerstorage.spaceclean.app.navigation.register

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.feature.home.HomeRoute
import com.aicleanerstorage.spaceclean.feature.home.HomeViewModel
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerHomeRoutes(
    navigator: AppNavigator,
    analyticsReporter: AnalyticsReporter,
    externalBlockingPromptActive: Boolean,
) {
    composable(AppDestination.Home.route) {
        val viewModel: HomeViewModel = koinViewModel()
        HomeRoute(
            navigator = navigator,
            viewModel = viewModel,
            analyticsReporter = analyticsReporter,
            externalBlockingPromptActive = externalBlockingPromptActive,
        )
    }
    composable(AppDestination.HomeFileManager.route) {
        val viewModel: HomeViewModel = koinViewModel()
        HomeRoute(
            navigator = navigator,
            viewModel = viewModel,
            analyticsReporter = analyticsReporter,
            externalBlockingPromptActive = externalBlockingPromptActive,
            initialTabIndex = 1,
        )
    }
    composable(AppDestination.HomeToolbox.route) {
        val viewModel: HomeViewModel = koinViewModel()
        HomeRoute(
            navigator = navigator,
            viewModel = viewModel,
            analyticsReporter = analyticsReporter,
            externalBlockingPromptActive = externalBlockingPromptActive,
            initialTabIndex = 2,
        )
    }
}
