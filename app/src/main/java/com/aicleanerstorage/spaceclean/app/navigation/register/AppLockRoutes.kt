package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.ui.AppLockRoute
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerAppLockRoutes(
    navigator: NavHostAppNavigator,
    featureFlow: FeatureFlow,
) {
    composable<AppDestination.AppLock> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: AppLockViewModel = koinViewModel()
        AppLockRoute(navigator = pageNavigator, viewModel = viewModel, featureFlow = featureFlow)
    }
}
