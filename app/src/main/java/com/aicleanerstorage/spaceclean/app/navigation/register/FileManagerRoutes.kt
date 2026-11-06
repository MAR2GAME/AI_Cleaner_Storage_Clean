package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember
import com.aicleanerstorage.spaceclean.app.navigation.owner.FeatureOwner
import com.aicleanerstorage.spaceclean.app.navigation.owner.FeatureOwnerRegistry
import com.aicleanerstorage.spaceclean.app.navigation.owner.rememberFileManagerOwnerState
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
        val owner = remember(entry, navController) {
            FeatureOwnerRegistry.resolve(entry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        PhotosManagerRoute(pageNavigator, koinViewModel<PhotosManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), featureFlow, permissionState)
    }
    composable<AppDestination.PhotosDetail> { backStackEntry ->
        val owner = remember(backStackEntry, navController) {
            FeatureOwnerRegistry.resolve(backStackEntry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val viewModel: PhotosManagerViewModel = koinViewModel(viewModelStoreOwner = owner.ownerEntry)
        PhotosFileDetailRoute(pageNavigator, viewModel, backStackEntry.toRoute<AppDestination.PhotosDetail>().initialIndex.coerceAtLeast(0), permissionState)
    }
    }
    navigation<SimilarPhotosFlow>(startDestination = AppDestination.SimilarPhotosManager) {
    composable<AppDestination.SimilarPhotosManager> { entry ->
        val owner = remember(entry, navController) {
            FeatureOwnerRegistry.resolve(entry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        SimilarPhotosManagerRoute(pageNavigator, koinViewModel<SimilarPhotosManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), featureFlow, permissionState)
    }
    composable<AppDestination.SimilarPhotosDetail> { backStackEntry ->
        val owner = remember(backStackEntry, navController) {
            FeatureOwnerRegistry.resolve(backStackEntry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val viewModel: SimilarPhotosManagerViewModel = koinViewModel(viewModelStoreOwner = owner.ownerEntry)
        SimilarPhotosFileDetailRoute(pageNavigator, viewModel, backStackEntry.toRoute<AppDestination.SimilarPhotosDetail>().initialIndex.coerceAtLeast(0), permissionState)
    }
    }
    composable<AppDestination.PhotoPrivacyManager> { entry ->
        val owner = remember(entry, navController) {
            FeatureOwnerRegistry.resolve(entry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        PhotoPrivacyManagerRoute(pageNavigator, koinViewModel<PhotoPrivacyManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), featureFlow, permissionState)
    }
    navigation<ScreenshotsFlow>(startDestination = AppDestination.ScreenshotsManager) {
    composable<AppDestination.ScreenshotsManager> { entry ->
        val owner = remember(entry, navController) {
            FeatureOwnerRegistry.resolve(entry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        ScreenshotsManagerRoute(pageNavigator, koinViewModel<ScreenshotsManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), featureFlow, permissionState)
    }
    composable<AppDestination.ScreenshotsDetail> { backStackEntry ->
        val owner = remember(backStackEntry, navController) {
            FeatureOwnerRegistry.resolve(backStackEntry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val viewModel: ScreenshotsManagerViewModel = koinViewModel(viewModelStoreOwner = owner.ownerEntry)
        ScreenshotsFileDetailRoute(pageNavigator, viewModel, backStackEntry.toRoute<AppDestination.ScreenshotsDetail>().initialIndex.coerceAtLeast(0), permissionState)
    }
    }
    navigation<VideosFlow>(startDestination = AppDestination.VideosManager) {
    composable<AppDestination.VideosManager> { entry ->
        val owner = remember(entry, navController) {
            FeatureOwnerRegistry.resolve(entry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        VideosManagerRoute(pageNavigator, koinViewModel<VideosManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), featureFlow, permissionState)
    }
    composable<AppDestination.VideosDetail> { backStackEntry ->
        val owner = remember(backStackEntry, navController) {
            FeatureOwnerRegistry.resolve(backStackEntry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val viewModel: VideosManagerViewModel = koinViewModel(viewModelStoreOwner = owner.ownerEntry)
        VideosFileDetailRoute(pageNavigator, viewModel, backStackEntry.toRoute<AppDestination.VideosDetail>().initialIndex.coerceAtLeast(0), permissionState)
    }
    }
    navigation<AudiosFlow>(startDestination = AppDestination.AudiosManager) {
    composable<AppDestination.AudiosManager> { entry ->
        val owner = remember(entry, navController) {
            FeatureOwnerRegistry.resolve(entry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        AudiosManagerRoute(pageNavigator, koinViewModel<AudiosManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), featureFlow, permissionState)
    }
    composable<AppDestination.AudiosDetail> { backStackEntry ->
        val owner = remember(backStackEntry, navController) {
            FeatureOwnerRegistry.resolve(backStackEntry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val viewModel: AudiosManagerViewModel = koinViewModel(viewModelStoreOwner = owner.ownerEntry)
        AudiosFileDetailRoute(pageNavigator, viewModel, backStackEntry.toRoute<AppDestination.AudiosDetail>().initialIndex.coerceAtLeast(0), permissionState)
    }
    }
    navigation<LargeFilesFlow>(startDestination = AppDestination.LargeFilesManager) {
    composable<AppDestination.LargeFilesManager> { entry ->
        val owner = remember(entry, navController) {
            FeatureOwnerRegistry.resolve(entry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        LargeFilesManagerRoute(pageNavigator, koinViewModel<LargeFilesManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), featureFlow, permissionState)
    }
    composable<AppDestination.LargeFilesDetail> { backStackEntry ->
        val owner = remember(backStackEntry, navController) {
            FeatureOwnerRegistry.resolve(backStackEntry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val viewModel: LargeFilesManagerViewModel = koinViewModel(viewModelStoreOwner = owner.ownerEntry)
        LargeFilesFileDetailRoute(pageNavigator, viewModel, backStackEntry.toRoute<AppDestination.LargeFilesDetail>().initialIndex.coerceAtLeast(0), permissionState)
    }
    }
    composable<AppDestination.DuplicateFilesManager> { entry ->
        val owner = remember(entry, navController) {
            FeatureOwnerRegistry.resolve(entry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        DuplicateFilesManagerRoute(pageNavigator, koinViewModel<DuplicateFilesManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), featureFlow, permissionState)
    }
    navigation<DocumentsFlow>(startDestination = AppDestination.DocumentsManager) {
    composable<AppDestination.DocumentsManager> { entry ->
        val owner = remember(entry, navController) {
            FeatureOwnerRegistry.resolve(entry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        DocumentsManagerRoute(pageNavigator, koinViewModel<DocumentsManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), featureFlow, permissionState)
    }
    composable<AppDestination.DocumentsDetail> { backStackEntry ->
        val owner = remember(backStackEntry, navController) {
            FeatureOwnerRegistry.resolve(backStackEntry, navController) as FeatureOwner.FileManager
        }
        val permissionState = rememberFileManagerOwnerState(owner.ownerEntry)
        val pageNavigator = remember(navigator, backStackEntry) { navigator.forEntry(backStackEntry) }
        val viewModel: DocumentsManagerViewModel = koinViewModel(viewModelStoreOwner = owner.ownerEntry)
        DocumentsFileDetailRoute(pageNavigator, viewModel, backStackEntry.toRoute<AppDestination.DocumentsDetail>().initialIndex.coerceAtLeast(0), permissionState)
    }
    }
}
