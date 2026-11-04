package com.aicleanerstorage.spaceclean.app.navigation.register

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.common.ui.components.ToolFeatureBanners
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.presentation.IntentInterceptor
import com.aicleanerstorage.spaceclean.feature.junkclean.ui.JunkCleanRoute
import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.JunkCleanViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerCleanRoutes(
    navigator: AppNavigator,
    featureFlow: FeatureFlow,
    completionAdInterceptor: IntentInterceptor,
) {
    composable(AppDestination.JunkClean.route) {
        val viewModel: JunkCleanViewModel = koinViewModel()
        JunkCleanRoute(
            viewModel = viewModel,
            featureFlow = featureFlow,
            completionAdInterceptor = completionAdInterceptor,
            onNavigateBack = { navigator.back() },
            onNavigateHome = { navigator.home() },
            onNavigateHomeAfterComplete = { navigator.home() },
            resultTools = { modifier, enabled ->
                ToolFeatureBanners(
                    modifier = modifier,
                    excludedDestinations = setOf(AppDestination.JunkClean),
                    onNavigateTool = { destination -> if (enabled) navigator.resetTo(destination) },
                )
            },
        )
    }
}
