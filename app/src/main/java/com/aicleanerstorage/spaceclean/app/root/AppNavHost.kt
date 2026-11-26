package com.aicleanerstorage.spaceclean.app.root

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdGateway
import com.aicleanerstorage.spaceclean.app.integration.Mar2Navigator
import com.aicleanerstorage.spaceclean.app.integration.NotificationRouteCoordinator
import com.aicleanerstorage.spaceclean.app.integration.NotificationRoutePhase
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
import com.aicleanerstorage.spaceclean.base.navigation.MainTabRequest
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AppNavHost(
    navController: NavHostController,
    splashPaused: Boolean,
    notificationPermissionUiActive: Boolean,
    splashNotificationPermissionReleased: Boolean,
    notificationRoutes: NotificationRouteCoordinator,
) {
    val activity = LocalContext.current.findActivity()
    val navigator = remember(navController, activity) { Mar2Navigator(navController, activity) }
    val pendingNotification by notificationRoutes.pending.collectAsStateWithLifecycle()
    val currentEntry by navController.currentBackStackEntryAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val hostLifecycleState by lifecycleOwner.lifecycle.currentStateAsState()
    val entryLifecycleState by
        (currentEntry?.lifecycle ?: lifecycleOwner.lifecycle).currentStateAsState()
    var inFlightForegroundAdId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(
        pendingNotification,
        currentEntry,
        entryLifecycleState,
        hostLifecycleState,
        notificationPermissionUiActive,
    ) {
        val pending = pendingNotification ?: return@LaunchedEffect
        val destination = currentEntry?.destination ?: return@LaunchedEffect
        val action = pending.action
        if (destination.hasRoute<AppDestination.Splash>() ||
            destination.hasRoute<AppDestination.OnboardingScan>()
        ) {
            return@LaunchedEffect
        }
        // Starting AdActivity while the host is still transitioning (Splash -> Main) or while a
        // permission/system dialog is on top pauses the ad request before it commits; the SDK then
        // closes the loading page immediately. Wait until the real destination is RESUMED.
        if (notificationPermissionUiActive ||
            entryLifecycleState != Lifecycle.State.RESUMED ||
            hostLifecycleState != Lifecycle.State.RESUMED
        ) {
            return@LaunchedEffect
        }
        val continueNavigation: () -> Unit = navigation@{
            if (!notificationRoutes.beginNavigation(action.id)) return@navigation
            runCatching { navigator.resetToRootForNotification(action.destination) }
                .onSuccess { notificationRoutes.consume(action.id) }
            Unit
        }
        when (pending.phase) {
            NotificationRoutePhase.WAITING_FOREGROUND_AD -> {
                if (activity == null) {
                    notificationRoutes.skipForegroundAd(action.id)
                    continueNavigation()
                    return@LaunchedEffect
                }
                if (!notificationRoutes.beginForegroundAd(action.id)) return@LaunchedEffect
                inFlightForegroundAdId = action.id
                Mar2AdGateway.showForegroundAd(
                    activity = activity,
                    currentRoute = destination.route.orEmpty(),
                    onContinue = {
                        if (inFlightForegroundAdId == action.id) {
                            inFlightForegroundAdId = null
                            notificationRoutes.foregroundAdFinished(action.id)
                            continueNavigation()
                        }
                    },
                )
            }

            NotificationRoutePhase.SHOWING_FOREGROUND_AD -> {
                if (inFlightForegroundAdId == action.id) return@LaunchedEffect
                notificationRoutes.foregroundAdFinished(action.id)
                continueNavigation()
            }

            NotificationRoutePhase.PENDING_NAVIGATION -> continueNavigation()
            NotificationRoutePhase.NAVIGATING -> Unit
        }
    }

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
                notificationPermissionReleased = splashNotificationPermissionReleased,
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
                .getStateFlow<Any?>(MAIN_TAB_REQUEST, null)
                .collectAsStateWithLifecycle()
            LaunchedEffect(requestedTab) {
                val tab = MainTabRequest.decode(requestedTab) ?: return@LaunchedEffect
                viewModel.dispatch(
                    com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeAction
                        .TabSelected(tab.ordinal),
                )
                entry.savedStateHandle[MAIN_TAB_REQUEST] = null
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
                navigator = navigator,
                viewModel = viewModel,
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
        fileManagerRoutes(
            navController = navController,
            navigator = navigator,
        )

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

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
