package com.aicleanerstorage.spaceclean.app.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.aicleanerstorage.spaceclean.app.view.common.permission.NotificationPermissionPrompt

@Composable
fun AppRootView() {
    val navController = rememberNavController()
    var permissionUiActive by remember { mutableStateOf(false) }

    AppNavHost(
        navController = navController,
        splashPaused = permissionUiActive,
        notificationPermissionUiActive = permissionUiActive,
    )
    NotificationPermissionPrompt(
        onActiveChanged = { permissionUiActive = it },
    )
}
