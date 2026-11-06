package com.aicleanerstorage.spaceclean.app.navigation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.register.registerAntiVirusRoutes
import com.aicleanerstorage.spaceclean.app.navigation.register.registerAppLockRoutes
import com.aicleanerstorage.spaceclean.app.navigation.register.registerCleanRoutes
import com.aicleanerstorage.spaceclean.app.navigation.register.registerFileManagerRoutes
import com.aicleanerstorage.spaceclean.app.navigation.register.registerHomeRoutes
import com.aicleanerstorage.spaceclean.app.navigation.register.registerOnboardingRoute
import com.aicleanerstorage.spaceclean.app.navigation.register.registerSettingsRoutes
import com.aicleanerstorage.spaceclean.app.navigation.register.registerSplashRoute
import com.aicleanerstorage.spaceclean.app.navigation.register.registerToolboxRoutes
import com.aicleanerstorage.spaceclean.app.runtime.AppRuntimeBindings
import com.aicleanerstorage.spaceclean.app.runtime.NavigationEffectHost
import com.aicleanerstorage.spaceclean.app.runtime.NotificationLaunchEffect
import com.aicleanerstorage.spaceclean.app.runtime.onboardingScanFinished
import com.aicleanerstorage.spaceclean.app.runtime.onboardingCompletion
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchCoordinator

@Composable
internal fun AppNavGraph(
    navController: NavHostController,
    launchCoordinator: AppLaunchCoordinator,
    runtime: AppRuntimeBindings,
    splashPaused: Boolean,
    notificationPermissionUiActive: Boolean,
) {
    val navigationScope = rememberCoroutineScope()
    val navigator =
        remember(navController) {
            NavHostAppNavigator(
                navController = navController,
                scope = navigationScope,
            )
        }

    DisposableEffect(navController, navigator) {
        val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, _, _ ->
            navigator.sourceChanged(navController.currentBackStackEntry?.id)
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
            navigator.close()
        }
    }

    val entry by navController.currentBackStackEntryAsState()
    val pendingRequest by launchCoordinator.pendingRequest.collectAsStateWithLifecycle()
    NotificationLaunchEffect(pendingRequest, entry?.destination?.route?.substringBefore('?')) {
        entry?.let { navigator.forEntry(it).navigate(AppDestination.Splash) }
    }
    NavigationEffectHost(navigator.events, entry)
    FeatureFlowOwners(entry, navController, navigator, runtime) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            AppRouteTransitions.enterTransition(
                scope = this,
                initialRoute = initialState.destination.route,
                targetRoute = targetState.destination.route,
            )
        },
        exitTransition = {
            AppRouteTransitions.exitTransition(
                scope = this,
                initialRoute = initialState.destination.route,
                targetRoute = targetState.destination.route,
            )
        },
        popEnterTransition = {
            AppRouteTransitions.popEnterTransition(
                scope = this,
                initialRoute = initialState.destination.route,
                targetRoute = targetState.destination.route,
            )
        },
        popExitTransition = {
            AppRouteTransitions.popExitTransition(
                scope = this,
                initialRoute = initialState.destination.route,
                targetRoute = targetState.destination.route,
            )
        },
    ) {
        registerSplashRoute(
            navigator = navigator,
            launchCoordinator = launchCoordinator,
            externalActivityTracker = runtime.externalActivities,
            splashPermissionPaused = splashPaused,
            analyticsReporter = runtime.analyticsReporter,
        )
        registerOnboardingRoute(
            navigator = navigator,
            onScanFinished = runtime::onboardingScanFinished,
            onCompletion = runtime::onboardingCompletion,
            analyticsReporter = runtime.analyticsReporter,
        )
        registerHomeRoutes(
            navigator = navigator,
            analyticsReporter = runtime.analyticsReporter,
            externalBlockingPromptActive = notificationPermissionUiActive,
        )
        registerCleanRoutes(
            navigator = navigator,
            featureFlow = runtime.featureFlow,
        )
        registerAntiVirusRoutes(
            navController = navController,
            navigator = navigator,
            featureFlow = runtime.featureFlow,
            externalActivities = runtime.externalActivities,
        )
        registerAppLockRoutes(
            navigator = navigator,
            featureFlow = runtime.featureFlow,
        )
        registerToolboxRoutes(
            navigator = navigator,
            featureFlow = runtime.featureFlow,
            externalActivities = runtime.externalActivities,
        )
        registerFileManagerRoutes(
            navController = navController,
            navigator = navigator,
            featureFlow = runtime.featureFlow,
        )
        registerSettingsRoutes(
            navigator = navigator,
            externalActivities = runtime.externalActivities,
            analyticsReporter = runtime.analyticsReporter,
        )
    }
}
}
