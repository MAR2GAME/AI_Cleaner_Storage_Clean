package com.aicleanerstorage.spaceclean.app.root

import org.koin.core.qualifier.named

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigatorImpl
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.designsystem.component.ToolFeatureBanners
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.VirusEffectHandler
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.app.view.junkclean.ui.JunkCleanRoute

import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.base.navigation.MAIN_TAB_REQUEST
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowHost
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanViewModel
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.AntiVirusRoute
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.DeepScanVirusRoute
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.NoVirusResultRoute
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.QuickScanVirusRoute
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.ScanVirusResultRoute
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockViewModel
import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockRoute
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageViewModel
import com.aicleanerstorage.spaceclean.app.view.appusage.ui.AppUsageRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.MediaManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.audios.ui.AudiosManagerRoute
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.battery.ui.BatteryInfoRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.MediaFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileListDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.PhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.ScreenshotsFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.SimilarPhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.DeviceInfoRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.documents.ui.DocumentsManagerRoute
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.duplicates.ui.DuplicateFilesManagerRoute
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeViewModel
import com.aicleanerstorage.spaceclean.app.view.home.ui.HomeRoute
import com.aicleanerstorage.spaceclean.app.view.largefiles.ui.LargeFilesManagerRoute
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesViewModel
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanDevicesRoute
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
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.ui.PhotoPrivacyManagerRoute
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photos.ui.PhotosManagerRoute
import com.aicleanerstorage.spaceclean.app.view.screenshots.interaction.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.screenshots.ui.ScreenshotsManagerRoute
import com.aicleanerstorage.spaceclean.app.view.settings.ui.SettingsRoute
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.similarphotos.ui.SimilarPhotosManagerRoute
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashAction
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashEvent
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashViewModel
import com.aicleanerstorage.spaceclean.app.view.splash.ui.SplashLegalDocument
import com.aicleanerstorage.spaceclean.app.view.splash.ui.SplashScreen
import com.aicleanerstorage.spaceclean.app.view.videos.ui.VideosManagerRoute
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.WhatsAppCleanerRoute
import com.aicleanerstorage.spaceclean.base.navigation.MainTab
import com.aicleanerstorage.spaceclean.base.util.ExternalLinkUtil
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AppNavHost(
    navController: NavHostController,
    splashPaused: Boolean,
    notificationPermissionUiActive: Boolean,
) {
    val navigator = remember(navController) { AppNavigatorImpl(navController) }

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
        registerSplashRoute(
            navigator = navigator,
            splashPermissionPaused = splashPaused,
        )
        registerOnboardingRoute(navigator = navigator)
        registerHomeRoutes(
            navigator = navigator,
            externalBlockingPromptActive = notificationPermissionUiActive,
        )
        registerCleanRoutes(
            navigator = navigator,
        )
        registerAntiVirusRoutes(
            navController = navController,
            navigator = navigator,
        )
        registerAppLockRoutes(
            navigator = navigator,
        )
        registerToolboxRoutes(
            navigator = navigator,
        )
        registerFileManagerRoutes(
            navController = navController,
            navigator = navigator,
        )
        registerSettingsRoutes(
            navigator = navigator,
        )
    }
}




internal fun NavGraphBuilder.registerAntiVirusRoutes(
    navController: NavHostController,
    navigator: AppNavigator,
) {
    composable<AppDestination.AntiVirus> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AntiVirus>() }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        VirusEffectHandler(navigator, viewModel)
        AntiVirusRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusQuickScan> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<AppDestination.AntiVirus>()
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VirusEffectHandler(navigator, viewModel)
        QuickScanVirusRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusDeepScan> { backStackEntry ->
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<AppDestination.AntiVirus>()
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VirusEffectHandler(navigator, viewModel)
        DeepScanVirusRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusResult> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<AppDestination.AntiVirus>()
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VirusEffectHandler(navigator, viewModel)
        ScanVirusResultRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.NoVirusResult> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AntiVirus>() }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        VirusEffectHandler(navigator, viewModel)
        NoVirusResultRoute(navigator, viewModel)
    }
}




internal fun NavGraphBuilder.registerAppLockRoutes(
    navigator: AppNavigator,
) {
    composable<AppDestination.AppLock> {
        val viewModel: AppLockViewModel = koinViewModel()
        AppLockRoute(navigator = navigator, viewModel = viewModel)
    }
}



internal fun NavGraphBuilder.registerCleanRoutes(
    navigator: AppNavigator,
) {
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
}





/**
 * 鏂囦欢绠＄悊 7 缁勶細鍒楄〃椤典笌璇︽儏椤靛湪 AppNavHost 涓嬪钩绾ф敞鍐岋紝璇︽儏椤电敤鍒楄〃鐩殑鍦扮�?back stack entry
 * 浣滀�?ViewModel 浣滅敤鍩燂紝浠庤€屼笌鍒楄〃椤靛叡浜悓涓€涓?ViewModel锛涙祦绋嬬骇 effect 鐢卞綋鍓嶉〉鐨?
 * [FileManagerFlowHost] 鏀堕泦锛堜换鎰忔椂鍒诲彧鏈変竴涓秷璐硅€咃級�?
 */
internal fun NavGraphBuilder.registerFileManagerRoutes(
    navController: NavHostController,
    navigator: AppNavigator,
) {
    composable<AppDestination.PhotosManager> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.PhotosManager>() }
        val viewModel: PhotosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            PhotosManagerRoute(
                navigator = navigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.PhotosDetail> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.PhotosManager>() }
        val viewModel: PhotosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            PhotosFileDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.PhotosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.SimilarPhotosManager> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.SimilarPhotosManager>() }
        val viewModel: SimilarPhotosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            SimilarPhotosManagerRoute(
                navigator = navigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.SimilarPhotosDetail> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.SimilarPhotosManager>() }
        val viewModel: SimilarPhotosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            SimilarPhotosFileDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.SimilarPhotosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    // 鐓х墖闅愮�?閲嶅鏂囦欢鏄嫭绔嬪崟椤碉紝entry 鑷韩鍗虫祦绋嬫牴锛屽悓鏍风敤瀹夸富鎵樼娴佺▼绾?effect�?
    composable<AppDestination.PhotoPrivacyManager> { entry ->
        val viewModel: PhotoPrivacyManagerViewModel = koinViewModel(viewModelStoreOwner = entry)
        FileManagerFlowHost(
            rootEntry = entry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            PhotoPrivacyManagerRoute(
                navigator = navigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.ScreenshotsManager> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.ScreenshotsManager>() }
        val viewModel: ScreenshotsManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            ScreenshotsManagerRoute(
                navigator = navigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.ScreenshotsDetail> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.ScreenshotsManager>() }
        val viewModel: ScreenshotsManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            ScreenshotsFileDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.ScreenshotsDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.VideosManager> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.VideosManager>() }
        val viewModel: MediaManagerViewModel = koinViewModel(
            qualifier = named<AppDestination.VideosManager>(),
            viewModelStoreOwner = rootEntry,
        )
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            VideosManagerRoute(
                navigator = navigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.VideosDetail> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.VideosManager>() }
        val viewModel: MediaManagerViewModel = koinViewModel(
            qualifier = named<AppDestination.VideosManager>(),
            viewModelStoreOwner = rootEntry,
        )
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            MediaFileDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.VideosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.AudiosManager> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AudiosManager>() }
        val viewModel: MediaManagerViewModel = koinViewModel(
            qualifier = named<AppDestination.AudiosManager>(),
            viewModelStoreOwner = rootEntry,
        )
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            AudiosManagerRoute(
                navigator = navigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.AudiosDetail> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AudiosManager>() }
        val viewModel: MediaManagerViewModel = koinViewModel(
            qualifier = named<AppDestination.AudiosManager>(),
            viewModelStoreOwner = rootEntry,
        )
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            MediaFileDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.AudiosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.LargeFilesManager> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.LargeFilesManager>() }
        val viewModel: FileListManagerViewModel = koinViewModel(
            qualifier = named<AppDestination.LargeFilesManager>(),
            viewModelStoreOwner = rootEntry,
        )
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            LargeFilesManagerRoute(
                navigator = navigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.LargeFilesDetail> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.LargeFilesManager>() }
        val viewModel: FileListManagerViewModel = koinViewModel(
            qualifier = named<AppDestination.LargeFilesManager>(),
            viewModelStoreOwner = rootEntry,
        )
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            FileListDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.LargeFilesDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.DuplicateFilesManager> { entry ->
        val viewModel: DuplicateFilesManagerViewModel = koinViewModel(viewModelStoreOwner = entry)
        FileManagerFlowHost(
            rootEntry = entry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            DuplicateFilesManagerRoute(
                navigator = navigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.DocumentsManager> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.DocumentsManager>() }
        val viewModel: FileListManagerViewModel = koinViewModel(
            qualifier = named<AppDestination.DocumentsManager>(),
            viewModelStoreOwner = rootEntry,
        )
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            DocumentsManagerRoute(
                navigator = navigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.DocumentsDetail> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.DocumentsManager>() }
        val viewModel: FileListManagerViewModel = koinViewModel(
            qualifier = named<AppDestination.DocumentsManager>(),
            viewModelStoreOwner = rootEntry,
        )
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            FileListDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.DocumentsDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }
}







internal fun NavGraphBuilder.registerHomeRoutes(
    navigator: AppNavigator,
    externalBlockingPromptActive: Boolean,
) {
    composable<AppDestination.Main> { entry ->
        val viewModel: HomeViewModel = koinViewModel()
        val initialTab = remember(entry) { entry.toRoute<AppDestination.Main>().initialTab }
        val requestedTab by entry.savedStateHandle.getStateFlow<String?>(MAIN_TAB_REQUEST, null)
            .collectAsStateWithLifecycle()
        LaunchedEffect(requestedTab) {
            requestedTab?.let {
                viewModel.dispatch(com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeAction.TabSelected(MainTab.valueOf(it).ordinal))
                entry.savedStateHandle[MAIN_TAB_REQUEST] = null
            }
        }
        HomeRoute(
            navigator = navigator,
            viewModel = viewModel,
            externalBlockingPromptActive = externalBlockingPromptActive,
            initialTabIndex = initialTab.ordinal,
        )
    }
}



internal fun NavGraphBuilder.registerOnboardingRoute(
    navigator: AppNavigator,
) {
    composable<AppDestination.OnboardingScan> {
        val viewModel: OnboardingScanViewModel = koinViewModel()
        OnboardingRoute(
            navigator = navigator,
            viewModel = viewModel,
        )
    }
}





internal fun NavGraphBuilder.registerSettingsRoutes(
    navigator: AppNavigator,
) {
    composable<AppDestination.Settings> {
        SettingsRoute(
            viewModel = koinViewModel(),
            termsUrl = com.aicleanerstorage.spaceclean.app.AppConfig.TERMS_OF_SERVICE_URL,
            privacyUrl = com.aicleanerstorage.spaceclean.app.AppConfig.PRIVACY_POLICY_URL,
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



internal fun NavGraphBuilder.registerSplashRoute(
    navigator: AppNavigator,
    splashPermissionPaused: Boolean,
) {
    composable<AppDestination.Splash> {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val viewModel: SplashViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        var externalLinkActive by remember { mutableStateOf(false) }

        LaunchedEffect(splashPermissionPaused) {
            viewModel.dispatch(SplashAction.PermissionPauseChanged(splashPermissionPaused))
        }
        LaunchedEffect(viewModel, navigator) {
            viewModel.event.collect { effect ->
                when (effect) {
                    is SplashEvent.Navigate -> navigator.replace(effect.destination)
                }
            }
        }
        DisposableEffect(lifecycleOwner, viewModel) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME && externalLinkActive) {
                    externalLinkActive = false
                    viewModel.dispatch(SplashAction.ExternalLinkStateChanged(active = false))
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        SplashScreen(
            state = state,
            onAction = viewModel::dispatch,
            onOpenLegalDocument = { document ->
                externalLinkActive = true
                viewModel.dispatch(SplashAction.ExternalLinkStateChanged(active = true))
                when (document) {
                    SplashLegalDocument.Terms -> ExternalLinkUtil.open(context, AppConfig.TERMS_OF_SERVICE_URL)
                    SplashLegalDocument.Privacy -> ExternalLinkUtil.open(context, AppConfig.PRIVACY_POLICY_URL)
                }
            },
        )
    }
}





internal fun NavGraphBuilder.registerToolboxRoutes(
    navigator: AppNavigator,
) {
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
    composable<AppDestination.NetworkScanDevices> {
        NetworkScanDevicesRoute(navigator, koinViewModel<NetworkScanDevicesViewModel>())
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
}
