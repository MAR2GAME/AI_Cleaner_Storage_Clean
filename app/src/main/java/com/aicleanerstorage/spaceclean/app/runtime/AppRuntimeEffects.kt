package com.aicleanerstorage.spaceclean.app.runtime

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.aicleanerstorage.spaceclean.core.contracts.ads.AdDriver
import com.aicleanerstorage.spaceclean.core.contracts.ads.ExternalAppOpenGuard
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.startup.AppLaunchRequest
import com.aicleanerstorage.spaceclean.core.contracts.ads.AdPreloadStage
import com.aicleanerstorage.spaceclean.app.platform.analytics.AnalyticsTracker

@Composable
internal fun AdLifecycleEffect(lifecycleOwner: LifecycleOwner, guard: ExternalAppOpenGuard) {
    DisposableEffect(lifecycleOwner, guard) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) guard.onHostResumed()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
internal fun RouteAnalyticsEffect(route: String?, context: Context, adDriver: AdDriver) {
    var previousRoute by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(route) {
        val current = route ?: return@LaunchedEffect
        if (current in AppDestination.homeRoutes) {
            AnalyticsTracker.trackHomeEntered(AnalyticsTracker.homepageReferrer(previousRoute))
            adDriver.preload(AdPreloadStage.MainPage)
        }
        AnalyticsTracker.featureForPrimaryRoute(current)?.let(AnalyticsTracker::trackCoreFeatureEntered)
        previousRoute = current
    }
}

@Composable
internal fun NotificationLaunchEffect(
    pendingRequest: AppLaunchRequest?,
    currentRoute: String?,
    onNavigateToSplash: () -> Unit,
) {
    LaunchedEffect(pendingRequest, currentRoute) {
        if (shouldRouteNotificationRequestToSplash(pendingRequest, currentRoute)) {
            onNavigateToSplash()
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
