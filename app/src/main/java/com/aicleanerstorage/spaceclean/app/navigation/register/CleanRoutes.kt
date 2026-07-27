package com.aicleanerstorage.spaceclean.app.navigation.register

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanRoute
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerCleanRoutes(
    navigator: AppNavigator,
    featureFlow: FeatureFlow,
) {
    composable(AppDestination.JunkClean.route) {
        val viewModel: JunkCleanViewModel = koinViewModel()
        JunkCleanRoute(
            viewModel = viewModel,
            permissionCoordinator = LocalPermissionCoordinator.current,
            featureFlow = featureFlow,
            onNavigateBack = { navigator.back() },
            onNavigateHome = { navigator.home() },
            onNavigateHomeAfterComplete = { navigator.home() },
            onNavigateTool = navigator::resetTo,
        )
    }
}
