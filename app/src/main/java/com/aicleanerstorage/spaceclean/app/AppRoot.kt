package com.aicleanerstorage.spaceclean.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aicleanerstorage.spaceclean.app.navigation.AppNavGraph
import com.aicleanerstorage.spaceclean.app.runtime.AppSessionCoordinator
import com.aicleanerstorage.spaceclean.app.runtime.NotificationLaunchEffect
import com.aicleanerstorage.spaceclean.app.runtime.RuntimeBusyReason
import com.aicleanerstorage.spaceclean.app.permission.AppPermissionHost
import com.aicleanerstorage.spaceclean.app.permission.NotificationPermissionHost
import com.aicleanerstorage.spaceclean.app.permission.NotificationPermissionViewModel
import com.aicleanerstorage.spaceclean.app.runtime.rememberAppRuntimeBindings
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchCoordinator
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppRoot(launchCoordinator: AppLaunchCoordinator) {
    val notificationPermissionViewModel: NotificationPermissionViewModel = koinViewModel()
    val cleanupHistory: CleanupHistoryStore = koinInject()
    val notificationState by notificationPermissionViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val pendingRequest by launchCoordinator.pendingRequest.collectAsStateWithLifecycle()

    val sessionCoordinator = remember { AppSessionCoordinator() }
    val runtimeBindings =
        rememberAppRuntimeBindings(
            sessionCoordinator = sessionCoordinator,
            cleanupHistory = cleanupHistory,
        )

    AppPermissionHost(
        externalActivityTracker = runtimeBindings.externalActivities,
        onPermissionFlowActiveChange = { sessionCoordinator.set(RuntimeBusyReason.Permission, it) },
    ) {
        AppNavGraph(
            navController = navController,
            launchCoordinator = launchCoordinator,
            runtime = runtimeBindings,
            splashPaused = notificationState.splashPaused,
            notificationPermissionUiActive = notificationState.permissionUiActive,
        )
        NotificationPermissionHost(
            viewModel = notificationPermissionViewModel,
            currentRoute = currentRoute,
            externalActivityTracker = runtimeBindings.externalActivities,
            onPermissionGranted = {},
        )
        NotificationLaunchEffect(pendingRequest, currentRoute, navController)
    }
}
