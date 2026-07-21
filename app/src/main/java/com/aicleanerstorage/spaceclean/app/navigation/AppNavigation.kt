package com.aicleanerstorage.spaceclean.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
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
import com.aicleanerstorage.spaceclean.app.runtime.startup.AppLaunchCoordinator

@Composable
internal fun AppNavGraph(
    navController: NavHostController,
    launchCoordinator: AppLaunchCoordinator,
    runtime: AppRuntimeBindings,
    splashPaused: Boolean,
    notificationPermissionUiActive: Boolean,
) {
    val navigator = remember(navController) { NavHostControllerAppNavigator(navController) }

    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash.route,
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
            externalActivityLauncher = runtime.externalActivities,
            splashPermissionPaused = splashPaused,
        )
        registerOnboardingRoute(
            navigator = navigator,
        )
        registerHomeRoutes(
            navigator = navigator,
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
        )
    }
}
