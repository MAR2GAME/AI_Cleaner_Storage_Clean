package com.aicleanerstorage.spaceclean.app.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockViewModel
import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockRoute
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageViewModel
import com.aicleanerstorage.spaceclean.app.view.appusage.ui.AppUsageRoute
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.battery.ui.BatteryInfoRoute
import com.aicleanerstorage.spaceclean.app.view.common.tool.ToolFeatureBanners
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.DeviceInfoRoute
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeViewModel
import com.aicleanerstorage.spaceclean.app.view.home.ui.HomeRoute
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.app.view.junkclean.ui.JunkCleanRoute
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanRoute
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedRoute
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.app.view.networkusage.ui.NetworkUsageRoute
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerViewModel
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.NotificationCleanerRoute
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingScanViewModel
import com.aicleanerstorage.spaceclean.app.view.onboarding.ui.OnboardingRoute
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsViewModel
import com.aicleanerstorage.spaceclean.app.view.permissions.ui.ManagePermissionsRoute
import com.aicleanerstorage.spaceclean.app.view.settings.ui.SettingsRoute
import com.aicleanerstorage.spaceclean.app.view.splash.ui.SplashRoute
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.WhatsAppCleanerRoute
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.MAIN_TAB_REQUEST
import com.aicleanerstorage.spaceclean.base.navigation.MainTab
import com.aicleanerstorage.spaceclean.base.navigation.NavControllerAppNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AppNavHost(
    navController: NavHostController,
    splashPaused: Boolean,
    notificationPermissionUiActive: Boolean,
) {
    val navigator = remember(navController) { NavControllerAppNavigator(navController) }

    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            AppRouteTransitions.enterTransition(
                scope = this,
                initialDestination = initialState.destination,
                targetDestination = targetState.destination,
            )
        },
        exitTransition = {
            AppRouteTransitions.exitTransition(
                scope = this,
                initialDestination = initialState.destination,
                targetDestination = targetState.destination,
            )
        },
        popEnterTransition = {
            AppRouteTransitions.popEnterTransition(
                scope = this,
                initialDestination = initialState.destination,
                targetDestination = targetState.destination,
            )
        },
        popExitTransition = {
            AppRouteTransitions.popExitTransition(
                scope = this,
                initialDestination = initialState.destination,
                targetDestination = targetState.destination,
            )
        },
    ) {
        // ---- Splash ----
        composable<AppDestination.Splash> {
            SplashRoute(
                navigator = navigator,
                splashPaused = splashPaused,
            )
        }

        // ---- Onboarding ----
        composable<AppDestination.OnboardingScan> {
            val viewModel: OnboardingScanViewModel = koinViewModel()
            OnboardingRoute(
                navigator = navigator,
                viewModel = viewModel,
            )
        }

        // ---- Home ----
        composable<AppDestination.Main> { entry ->
            val viewModel: HomeViewModel = koinViewModel()
            val initialTab = remember(entry) { entry.toRoute<AppDestination.Main>().initialTab }
            val requestedTab by entry.savedStateHandle
                .getStateFlow<String?>(MAIN_TAB_REQUEST, null)
                .collectAsStateWithLifecycle()
            LaunchedEffect(requestedTab) {
                requestedTab?.let {
                    viewModel.dispatch(
                        com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeAction
                            .TabSelected(MainTab.valueOf(it).ordinal),
                    )
                    entry.savedStateHandle[MAIN_TAB_REQUEST] = null
                }
            }
            HomeRoute(
                navigator = navigator,
                viewModel = viewModel,
                externalBlockingPromptActive = notificationPermissionUiActive,
                initialTabIndex = initialTab.ordinal,
            )
        }

        // ---- Clean ----
        composable<AppDestination.JunkClean> { entry ->
            val viewModel: JunkCleanViewModel = koinViewModel(viewModelStoreOwner = entry)
            JunkCleanRoute(
                viewModel = viewModel,
                onNavigateBack = { navigator.back() },
                onNavigateHome = { navigator.resetToRoot(AppDestination.Main()) },
                onNavigateHomeAfterComplete = { navigator.resetToRoot(AppDestination.Main()) },
                resultTools = { modifier, enabled ->
                    ToolFeatureBanners(
                        modifier = modifier,
                        excludedDestinations = setOf(AppDestination.JunkClean),
                        onNavigateTool = { target ->
                            if (enabled) navigator.resetToRoot(target)
                        },
                    )
                },
            )
        }

        // ---- AntiVirus ----
        virusRoutes(navController, navigator)

        // ---- AppLock ----
        composable<AppDestination.AppLock> {
            val viewModel: AppLockViewModel = koinViewModel()
            AppLockRoute(navigator = navigator, viewModel = viewModel)
        }

        // ---- Toolbox ----
        composable<AppDestination.DeviceInfo> {
            DeviceInfoRoute(
                navigator = navigator,
                viewModel = koinViewModel<DeviceInfoViewModel>(),
            )
        }
        composable<AppDestination.BatteryInfo> {
            BatteryInfoRoute(
                navigator = navigator,
                viewModel = koinViewModel<BatteryInfoViewModel>(),
            )
        }
        composable<AppDestination.AppUsage> {
            AppUsageRoute(
                navigator = navigator,
                viewModel = koinViewModel<AppUsageViewModel>(),
            )
        }
        composable<AppDestination.NetworkUsage> {
            NetworkUsageRoute(
                navigator = navigator,
                viewModel = koinViewModel<NetworkUsageViewModel>(),
            )
        }
        composable<AppDestination.NetworkScan> {
            NetworkScanRoute(
                navigator = navigator,
                viewModel = koinViewModel<NetworkScanViewModel>(),
            )
        }
        composable<AppDestination.NetworkSpeed> {
            NetworkSpeedRoute(
                navigator = navigator,
                viewModel = koinViewModel<NetworkSpeedViewModel>(),
            )
        }
        composable<AppDestination.WhatsAppCleaner> {
            WhatsAppCleanerRoute(
                navigator = navigator,
                viewModel = koinViewModel<WhatsAppCleanerViewModel>(),
            )
        }
        composable<AppDestination.NotificationCleaner> {
            NotificationCleanerRoute(
                navigator = navigator,
                viewModel = koinViewModel<NotificationCleanerViewModel>(),
            )
        }

        // ---- FileManager ----
        fileManagerRoutes(navController, navigator)

        // ---- Settings ----
        composable<AppDestination.Settings> {
            SettingsRoute(
                viewModel = koinViewModel(),
                termsUrl = BuildConfig.TERMS_OF_SERVICE_URL,
                privacyUrl = BuildConfig.PRIVACY_POLICY_URL,
                navigator = navigator,
            )
        }
        composable<AppDestination.ManagePermissions> {
            ManagePermissionsRoute(
                navigator = navigator,
                viewModel = koinViewModel<ManagePermissionsViewModel>(),
            )
        }
    }
}
