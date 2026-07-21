package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.runtime.featureflow.FeatureFlowRuntime
import com.aicleanerstorage.spaceclean.feature.applock.AppLockRoute
import com.aicleanerstorage.spaceclean.feature.applock.AppLockViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerAppLockRoutes(
    navigator: AppNavigator,
    featureFlow: FeatureFlowRuntime,
) {
    composable(AppDestination.AppLock.route) {
        val viewModel: AppLockViewModel = koinViewModel()
        AppLockRoute(navigator = navigator, viewModel = viewModel, featureFlow = featureFlow)
    }
}
