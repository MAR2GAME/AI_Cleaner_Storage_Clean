package com.aicleanerstorage.spaceclean.app.root

import com.aicleanerstorage.spaceclean.app.AppEventHost
import com.aicleanerstorage.spaceclean.app.AppView
import com.aicleanerstorage.spaceclean.app.AppViewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.aicleanerstorage.spaceclean.app.permission.NotificationPermissionPrompt
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppRootView(
    onNotificationPermissionGranted: () -> Unit,
    appViewModel: AppViewModel = koinViewModel(),
) {
    val navController = rememberNavController()
    var permissionUiActive by remember { mutableStateOf(false) }

    AppView(appViewModel)
    AppEventHost(appViewModel)
    AppNavHost(
        navController = navController,
        splashPaused = permissionUiActive,
        notificationPermissionUiActive = permissionUiActive,
    )
    NotificationPermissionPrompt(
        onGranted = onNotificationPermissionGranted,
        onActiveChanged = { permissionUiActive = it },
    )
}
