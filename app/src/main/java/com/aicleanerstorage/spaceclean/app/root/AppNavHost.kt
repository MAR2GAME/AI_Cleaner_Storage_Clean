package com.aicleanerstorage.spaceclean.app.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigatorImpl
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget
import com.aicleanerstorage.spaceclean.base.navigation.toAppDestination
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
import com.aicleanerstorage.spaceclean.app.view.audios.interaction.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.audios.ui.AudiosManagerRoute
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.battery.ui.BatteryInfoRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.AudiosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.DocumentsFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.LargeFilesFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.PhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.ScreenshotsFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.SimilarPhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.VideosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.DeviceInfoRoute
import com.aicleanerstorage.spaceclean.app.view.documents.interaction.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.documents.ui.DocumentsManagerRoute
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.duplicates.ui.DuplicateFilesManagerRoute
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeViewModel
import com.aicleanerstorage.spaceclean.app.view.home.ui.HomeRoute
import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesManagerViewModel
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
import com.aicleanerstorage.spaceclean.app.view.videos.interaction.VideosManagerViewModel
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
    navigator: AppNavigatorImpl,
) {
    composable<AppDestination.AntiVirus> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AntiVirus>() }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        VirusEffectHandler(pageNavigator, viewModel)
        AntiVirusRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusQuickScan> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<AppDestination.AntiVirus>()
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VirusEffectHandler(pageNavigator, viewModel)
        QuickScanVirusRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusDeepScan> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<AppDestination.AntiVirus>()
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VirusEffectHandler(pageNavigator, viewModel)
        DeepScanVirusRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.VirusResult> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<AppDestination.AntiVirus>()
        }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VirusEffectHandler(pageNavigator, viewModel)
        ScanVirusResultRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
        )
    }

    composable<AppDestination.NoVirusResult> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AntiVirus>() }
        val viewModel: VirusScanViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        VirusEffectHandler(pageNavigator, viewModel)
        NoVirusResultRoute(pageNavigator, viewModel)
    }
}




internal fun NavGraphBuilder.registerAppLockRoutes(
    navigator: AppNavigatorImpl,
) {
    composable<AppDestination.AppLock> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: AppLockViewModel = koinViewModel()
        AppLockRoute(navigator = pageNavigator, viewModel = viewModel)
    }
}



internal fun NavGraphBuilder.registerCleanRoutes(
    navigator: AppNavigatorImpl,
) {
    composable<AppDestination.JunkClean> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: JunkCleanViewModel = koinViewModel(viewModelStoreOwner = entry)
        JunkCleanRoute(
            viewModel = viewModel,
            onNavigateBack = { pageNavigator.back() },
            onNavigateHome = { pageNavigator.resetToRoot(AppDestination.Main()) },
            onNavigateHomeAfterComplete = { pageNavigator.resetToRoot(AppDestination.Main()) },
            resultTools = { modifier, enabled ->
                ToolFeatureBanners(
                    modifier = modifier,
                    excludedDestinations = setOf(ToolNavigationTarget.JunkClean),
                    onNavigateTool = { target ->
                        if (enabled) pageNavigator.resetToRoot(target.toAppDestination())
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
    navigator: AppNavigatorImpl,
) {
    composable<AppDestination.PhotosManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.PhotosManager>() }
        val viewModel: PhotosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            PhotosManagerRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.PhotosDetail> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.PhotosManager>() }
        val viewModel: PhotosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            PhotosFileDetailRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.PhotosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.SimilarPhotosManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.SimilarPhotosManager>() }
        val viewModel: SimilarPhotosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            SimilarPhotosManagerRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.SimilarPhotosDetail> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.SimilarPhotosManager>() }
        val viewModel: SimilarPhotosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            SimilarPhotosFileDetailRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.SimilarPhotosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    // 鐓х墖闅愮�?閲嶅鏂囦欢鏄嫭绔嬪崟椤碉紝entry 鑷韩鍗虫祦绋嬫牴锛屽悓鏍风敤瀹夸富鎵樼娴佺▼绾?effect�?
    composable<AppDestination.PhotoPrivacyManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: PhotoPrivacyManagerViewModel = koinViewModel(viewModelStoreOwner = entry)
        FileManagerFlowHost(
            rootEntry = entry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            PhotoPrivacyManagerRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.ScreenshotsManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.ScreenshotsManager>() }
        val viewModel: ScreenshotsManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            ScreenshotsManagerRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.ScreenshotsDetail> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.ScreenshotsManager>() }
        val viewModel: ScreenshotsManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            ScreenshotsFileDetailRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.ScreenshotsDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.VideosManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.VideosManager>() }
        val viewModel: VideosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            VideosManagerRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.VideosDetail> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.VideosManager>() }
        val viewModel: VideosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            VideosFileDetailRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.VideosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.AudiosManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AudiosManager>() }
        val viewModel: AudiosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            AudiosManagerRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.AudiosDetail> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AudiosManager>() }
        val viewModel: AudiosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            AudiosFileDetailRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.AudiosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.LargeFilesManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.LargeFilesManager>() }
        val viewModel: LargeFilesManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            LargeFilesManagerRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.LargeFilesDetail> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.LargeFilesManager>() }
        val viewModel: LargeFilesManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            LargeFilesFileDetailRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.LargeFilesDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.DuplicateFilesManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: DuplicateFilesManagerViewModel = koinViewModel(viewModelStoreOwner = entry)
        FileManagerFlowHost(
            rootEntry = entry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            DuplicateFilesManagerRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.DocumentsManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.DocumentsManager>() }
        val viewModel: DocumentsManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            DocumentsManagerRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                permissionState = permissionState,
            )
        }
    }
    composable<AppDestination.DocumentsDetail> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.DocumentsManager>() }
        val viewModel: DocumentsManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = pageNavigator,
            viewModel = viewModel,
            errorMessage = { it.errorMessage },
        ) { permissionState ->
            DocumentsFileDetailRoute(
                navigator = pageNavigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.DocumentsDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }
}







internal fun NavGraphBuilder.registerHomeRoutes(
    navigator: AppNavigatorImpl,
    externalBlockingPromptActive: Boolean,
) {
    composable<AppDestination.Main> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
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
            navigator = pageNavigator,
            viewModel = viewModel,
            externalBlockingPromptActive = externalBlockingPromptActive,
            initialTabIndex = initialTab.ordinal,
        )
    }
}



internal fun NavGraphBuilder.registerOnboardingRoute(
    navigator: AppNavigatorImpl,
) {
    composable<AppDestination.OnboardingScan> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val viewModel: OnboardingScanViewModel = koinViewModel()
        OnboardingRoute(
            navigator = pageNavigator,
            viewModel = viewModel,
        )
    }
}





internal fun NavGraphBuilder.registerSettingsRoutes(
    navigator: AppNavigatorImpl,
) {
    composable<AppDestination.Settings> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        SettingsRoute(
            viewModel = koinViewModel(),
            termsUrl = com.aicleanerstorage.spaceclean.app.AppConfig.TERMS_OF_SERVICE_URL,
            privacyUrl = com.aicleanerstorage.spaceclean.app.AppConfig.PRIVACY_POLICY_URL,
            navigator = pageNavigator,
        )
    }
    composable<AppDestination.ManagePermissions> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        ManagePermissionsRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<ManagePermissionsViewModel>(),
        )
    }
}



internal fun NavGraphBuilder.registerSplashRoute(
    navigator: AppNavigatorImpl,
    splashPermissionPaused: Boolean,
) {
    composable<AppDestination.Splash> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
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
                    is SplashEvent.Navigate -> pageNavigator.replace(effect.destination)
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
    navigator: AppNavigatorImpl,
) {
    composable<AppDestination.DeviceInfo> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        DeviceInfoRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<DeviceInfoViewModel>(),
        )
    }
    composable<AppDestination.BatteryInfo> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        BatteryInfoRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<BatteryInfoViewModel>(),
        )
    }
    composable<AppDestination.AppUsage> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        AppUsageRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<AppUsageViewModel>(),
        )
    }
    composable<AppDestination.NetworkUsage> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NetworkUsageRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<NetworkUsageViewModel>(),
        )
    }
    composable<AppDestination.NetworkScan> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NetworkScanRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<NetworkScanViewModel>(),
        )
    }
    composable<AppDestination.NetworkScanDevices> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NetworkScanDevicesRoute(pageNavigator, koinViewModel<NetworkScanDevicesViewModel>())
    }
    composable<AppDestination.NetworkSpeed> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NetworkSpeedRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<NetworkSpeedViewModel>(),
        )
    }
    composable<AppDestination.WhatsAppCleaner> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        WhatsAppCleanerRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<WhatsAppCleanerViewModel>(),
        )
    }
    composable<AppDestination.NotificationCleaner> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        NotificationCleanerRoute(
            navigator = pageNavigator,
            viewModel = koinViewModel<NotificationCleanerViewModel>(),
        )
    }
}
