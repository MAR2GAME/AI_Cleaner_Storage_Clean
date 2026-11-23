package com.aicleanerstorage.spaceclean.app.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aicleanerstorage.spaceclean.app.view.common.permission.NotificationPermissionPrompt
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

@Composable
fun AppRootView() {
    val navController = rememberNavController()
    var permissionUiActive by remember { mutableStateOf(false) }
    val currentEntry by navController.currentBackStackEntryAsState()
    val onHome = currentEntry?.destination?.hasRoute<AppDestination.Main>() == true

    AppNavHost(
        navController = navController,
        splashPaused = permissionUiActive,
        notificationPermissionUiActive = permissionUiActive,
    )
    NotificationPermissionPrompt(
        enabled = onHome,
        onActiveChanged = { permissionUiActive = it },
    )
}
