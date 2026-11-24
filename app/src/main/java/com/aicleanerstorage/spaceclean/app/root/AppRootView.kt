package com.aicleanerstorage.spaceclean.app.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.common.permission.NotificationPermissionPrompt
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.integration.NotificationRouteCoordinator

@Composable
fun AppRootView(
    notificationRoutes: NotificationRouteCoordinator,
    onHostStateChanged: (route: String?, pageAllowsAd: Boolean, operationPending: Boolean) -> Unit,
) {
    val navController = rememberNavController()
    var permissionUiActive by remember { mutableStateOf(false) }
    val currentEntry by navController.currentBackStackEntryAsState()
    val pendingNotification by notificationRoutes.pending.collectAsStateWithLifecycle()
    val onHome = currentEntry?.destination?.hasRoute<AppDestination.Main>() == true
    val onSplash =
        currentEntry == null ||
            currentEntry?.destination?.hasRoute<AppDestination.Splash>() == true

    LaunchedEffect(currentEntry, permissionUiActive, pendingNotification) {
        val route = currentEntry?.destination?.route
        val home = currentEntry?.destination?.hasRoute<AppDestination.Main>() == true
        onHostStateChanged(
            route,
            home,
            permissionUiActive || pendingNotification != null,
        )
    }

    AppNavHost(
        navController = navController,
        splashPaused = permissionUiActive,
        notificationPermissionUiActive = permissionUiActive,
        notificationRoutes = notificationRoutes,
    )
    NotificationPermissionPrompt(
        onSplash = onSplash,
        onHome = onHome,
        onActiveChanged = { permissionUiActive = it },
    )
}
