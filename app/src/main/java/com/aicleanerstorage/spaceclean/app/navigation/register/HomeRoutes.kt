package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember


import com.aicleanerstorage.spaceclean.core.navigation.MainTab

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.MAIN_TAB_REQUEST
import com.aicleanerstorage.spaceclean.feature.home.view.home.ui.HomeRoute
import com.aicleanerstorage.spaceclean.feature.home.view.home.presentation.HomeViewModel
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerHomeRoutes(
    navigator: NavHostAppNavigator,
    analyticsReporter: AnalyticsReporter,
    externalBlockingPromptActive: Boolean,
) {
    composable<AppDestination.Main> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: HomeViewModel = koinViewModel()
        val initialTab = remember(entry) { entry.toRoute<AppDestination.Main>().initialTab }
        val requestedTab by entry.savedStateHandle.getStateFlow<String?>(MAIN_TAB_REQUEST, null)
            .collectAsStateWithLifecycle()
        LaunchedEffect(requestedTab) {
            requestedTab?.let {
                viewModel.dispatch(com.aicleanerstorage.spaceclean.feature.home.view.home.presentation.HomeIntent.TabSelected(MainTab.valueOf(it).ordinal))
                entry.savedStateHandle[MAIN_TAB_REQUEST] = null
            }
        }
        HomeRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
            analyticsReporter = analyticsReporter,
            externalBlockingPromptActive = externalBlockingPromptActive,
            initialTabIndex = initialTab.ordinal,
        )
    }
}
