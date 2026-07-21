package com.aicleanerstorage.spaceclean.app.navigation.register

import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.runtime.external.ExternalActivityLauncher
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.feature.settings.ManagePermissionsRoute
import com.aicleanerstorage.spaceclean.feature.settings.SettingsRoute
import com.aicleanerstorage.spaceclean.feature.settings.ManagePermissionsViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerSettingsRoutes(
    navigator: AppNavigator,
    externalActivities: ExternalActivityLauncher,
) {
    composable(AppDestination.Settings.route) {
        SettingsRoute(navigator = navigator, externalActivities = externalActivities)
    }
    composable(AppDestination.ManagePermissions.route) {
        ManagePermissionsRoute(
            navigator = navigator,
            permissionCoordinator = LocalPermissionCoordinator.current,
            viewModel = koinViewModel<ManagePermissionsViewModel>(),
        )
    }
}
