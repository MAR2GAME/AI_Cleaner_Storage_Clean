package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator

import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.aicleanerstorage.spaceclean.app.navigation.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow

import com.aicleanerstorage.spaceclean.feature.files.view.audios.presentation.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.documents.presentation.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.largefiles.presentation.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.photos.presentation.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.presentation.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.presentation.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.videos.presentation.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.audios.ui.AudiosManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.AudiosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.DocumentsFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.LargeFilesFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.PhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.ScreenshotsFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.SimilarPhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.VideosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.ui.DuplicateFilesManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.view.documents.ui.DocumentsManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.view.largefiles.ui.LargeFilesManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.ui.PhotoPrivacyManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.view.photos.ui.PhotosManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.ui.ScreenshotsManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.ui.SimilarPhotosManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.view.videos.ui.VideosManagerRoute
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerFileManagerRoutes(
    navController: NavHostController,
    navigator: NavHostAppNavigator,
    featureFlow: FeatureFlow,
) {
    navigation<PhotosFlow>(startDestination = AppDestination.PhotosManager) {
    composable<AppDestination.PhotosManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        PhotosManagerRoute(pageNavigator, koinViewModel<PhotosManagerViewModel>(viewModelStoreOwner = remember(entry) { navController.getBackStackEntry<PhotosFlow>() }), featureFlow)
    }
    composable<AppDestination.PhotosDetail> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<PhotosFlow>()
        }
        val viewModel: PhotosManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        PhotosFileDetailRoute(pageNavigator, org.koin.compose.koinInject<com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway>(), viewModel, backStackEntry.toRoute<AppDestination.PhotosDetail>().initialIndex.coerceAtLeast(0))
    }
    }
    navigation<SimilarPhotosFlow>(startDestination = AppDestination.SimilarPhotosManager) {
    composable<AppDestination.SimilarPhotosManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        SimilarPhotosManagerRoute(pageNavigator, koinViewModel<SimilarPhotosManagerViewModel>(viewModelStoreOwner = remember(entry) { navController.getBackStackEntry<SimilarPhotosFlow>() }), featureFlow)
    }
    composable<AppDestination.SimilarPhotosDetail> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<SimilarPhotosFlow>()
        }
        val viewModel: SimilarPhotosManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        SimilarPhotosFileDetailRoute(pageNavigator, org.koin.compose.koinInject<com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway>(), viewModel, backStackEntry.toRoute<AppDestination.SimilarPhotosDetail>().initialIndex.coerceAtLeast(0))
    }
    }
    composable<AppDestination.PhotoPrivacyManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        PhotoPrivacyManagerRoute(pageNavigator, koinViewModel<PhotoPrivacyManagerViewModel>(), featureFlow)
    }
    navigation<ScreenshotsFlow>(startDestination = AppDestination.ScreenshotsManager) {
    composable<AppDestination.ScreenshotsManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        ScreenshotsManagerRoute(pageNavigator, koinViewModel<ScreenshotsManagerViewModel>(viewModelStoreOwner = remember(entry) { navController.getBackStackEntry<ScreenshotsFlow>() }), featureFlow)
    }
    composable<AppDestination.ScreenshotsDetail> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<ScreenshotsFlow>()
        }
        val viewModel: ScreenshotsManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        ScreenshotsFileDetailRoute(pageNavigator, org.koin.compose.koinInject<com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway>(), viewModel, backStackEntry.toRoute<AppDestination.ScreenshotsDetail>().initialIndex.coerceAtLeast(0))
    }
    }
    navigation<VideosFlow>(startDestination = AppDestination.VideosManager) {
    composable<AppDestination.VideosManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        VideosManagerRoute(pageNavigator, koinViewModel<VideosManagerViewModel>(viewModelStoreOwner = remember(entry) { navController.getBackStackEntry<VideosFlow>() }), featureFlow)
    }
    composable<AppDestination.VideosDetail> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<VideosFlow>()
        }
        val viewModel: VideosManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VideosFileDetailRoute(pageNavigator, org.koin.compose.koinInject<com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway>(), viewModel, backStackEntry.toRoute<AppDestination.VideosDetail>().initialIndex.coerceAtLeast(0))
    }
    }
    navigation<AudiosFlow>(startDestination = AppDestination.AudiosManager) {
    composable<AppDestination.AudiosManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        AudiosManagerRoute(pageNavigator, koinViewModel<AudiosManagerViewModel>(viewModelStoreOwner = remember(entry) { navController.getBackStackEntry<AudiosFlow>() }), featureFlow)
    }
    composable<AppDestination.AudiosDetail> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<AudiosFlow>()
        }
        val viewModel: AudiosManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        AudiosFileDetailRoute(pageNavigator, org.koin.compose.koinInject<com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway>(), viewModel, backStackEntry.toRoute<AppDestination.AudiosDetail>().initialIndex.coerceAtLeast(0))
    }
    }
    navigation<LargeFilesFlow>(startDestination = AppDestination.LargeFilesManager) {
    composable<AppDestination.LargeFilesManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        LargeFilesManagerRoute(pageNavigator, koinViewModel<LargeFilesManagerViewModel>(viewModelStoreOwner = remember(entry) { navController.getBackStackEntry<LargeFilesFlow>() }), featureFlow)
    }
    composable<AppDestination.LargeFilesDetail> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<LargeFilesFlow>()
        }
        val viewModel: LargeFilesManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        LargeFilesFileDetailRoute(pageNavigator, org.koin.compose.koinInject<com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway>(), viewModel, backStackEntry.toRoute<AppDestination.LargeFilesDetail>().initialIndex.coerceAtLeast(0))
    }
    }
    composable<AppDestination.DuplicateFilesManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        DuplicateFilesManagerRoute(pageNavigator, koinViewModel<DuplicateFilesManagerViewModel>(), featureFlow)
    }
    navigation<DocumentsFlow>(startDestination = AppDestination.DocumentsManager) {
    composable<AppDestination.DocumentsManager> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        DocumentsManagerRoute(pageNavigator, koinViewModel<DocumentsManagerViewModel>(viewModelStoreOwner = remember(entry) { navController.getBackStackEntry<DocumentsFlow>() }), featureFlow)
    }
    composable<AppDestination.DocumentsDetail> { backStackEntry ->
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val parentEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry<DocumentsFlow>()
        }
        val viewModel: DocumentsManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        DocumentsFileDetailRoute(pageNavigator, org.koin.compose.koinInject<com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway>(), viewModel, backStackEntry.toRoute<AppDestination.DocumentsDetail>().initialIndex.coerceAtLeast(0))
    }
    }
}
