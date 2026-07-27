package com.aicleanerstorage.spaceclean.app.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchRequest

@Composable
internal fun NotificationLaunchEffect(
    pendingRequest: AppLaunchRequest?,
    currentRoute: String?,
    navController: NavHostController,
) {
    LaunchedEffect(pendingRequest, currentRoute) {
        if (shouldRouteNotificationRequestToSplash(pendingRequest, currentRoute)) {
            navController.navigate(AppDestination.Splash.route) { launchSingleTop = true }
        }
    }
}

internal fun shouldRouteNotificationRequestToSplash(
    pendingRequest: AppLaunchRequest?,
    currentRoute: String?,
): Boolean =
    pendingRequest is AppLaunchRequest.NotificationTarget &&
        currentRoute != null &&
        currentRoute != AppDestination.Splash.route
