package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.core.ui.components.ToolFeatureBanners
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.presentation.intent.UiRequestBroker
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.ui.JunkCleanRoute
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerCleanRoutes(
    navigator: NavHostAppNavigator,
    featureFlow: FeatureFlow,
) {
    composable<AppDestination.JunkClean> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: JunkCleanViewModel = koinViewModel()
        val uiRequestBroker: UiRequestBroker = org.koin.compose.koinInject()
        JunkCleanRoute(
            viewModel = viewModel,
            featureFlow = featureFlow,
            uiRequestBroker = uiRequestBroker,
            onNavigateBack = { pageNavigator.back() },
            onNavigateHome = { pageNavigator.backToMain() },
            onNavigateHomeAfterComplete = { pageNavigator.backToMain() },
            resultTools = { modifier, enabled ->
                ToolFeatureBanners(
                    modifier = modifier,
                    excludedDestinations = setOf(ToolNavigationTarget.JunkClean),
                    onNavigateTool = { target -> if (enabled) pageNavigator.resetTo(target.toAppDestination()) },
                )
            },
        )
    }
}
