package com.aicleanerstorage.spaceclean.app.navigation.register

import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.runtime.featureflow.FeatureFlowRuntime
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.app.navigation.DETAIL_INITIAL_INDEX_ARG
import com.aicleanerstorage.spaceclean.feature.files.audios.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.documents.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.largefiles.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.photos.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.photoprivacy.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.screenshots.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.similarphotos.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.videos.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.audios.AudiosManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.shared.AudiosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.shared.DocumentsFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.shared.LargeFilesFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.shared.PhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.shared.ScreenshotsFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.shared.SimilarPhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.shared.VideosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFilesManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.documents.DocumentsManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.largefiles.LargeFilesManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.photoprivacy.PhotoPrivacyManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.photos.PhotosManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.screenshots.ScreenshotsManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.similarphotos.SimilarPhotosManagerRoute
import com.aicleanerstorage.spaceclean.feature.files.videos.VideosManagerRoute
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerFileManagerRoutes(
    navController: NavHostController,
    navigator: AppNavigator,
    featureFlow: FeatureFlowRuntime,
) {
    composable(AppDestination.PhotosManager.route) {
        PhotosManagerRoute(navigator, koinViewModel<PhotosManagerViewModel>(), featureFlow)
    }
    composable(AppDestination.PhotosDetail.detailPattern()) { backStackEntry ->
        val parentEntry = remember(backStackEntry, navController) {
            navController.fileManagerViewModelOwnerOr(AppDestination.PhotosManager, backStackEntry)
        }
        val viewModel: PhotosManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        PhotosFileDetailRoute(navigator, LocalPermissionCoordinator.current, viewModel, backStackEntry.initialIndex())
    }
    composable(AppDestination.SimilarPhotosManager.route) {
        SimilarPhotosManagerRoute(navigator, koinViewModel<SimilarPhotosManagerViewModel>(), featureFlow)
    }
    composable(AppDestination.SimilarPhotosDetail.detailPattern()) { backStackEntry ->
        val parentEntry = remember(backStackEntry, navController) {
            navController.fileManagerViewModelOwnerOr(AppDestination.SimilarPhotosManager, backStackEntry)
        }
        val viewModel: SimilarPhotosManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        SimilarPhotosFileDetailRoute(navigator, LocalPermissionCoordinator.current, viewModel, backStackEntry.initialIndex())
    }
    composable(AppDestination.PhotoPrivacyManager.route) {
        PhotoPrivacyManagerRoute(navigator, koinViewModel<PhotoPrivacyManagerViewModel>(), featureFlow)
    }
    composable(AppDestination.ScreenshotsManager.route) {
        ScreenshotsManagerRoute(navigator, koinViewModel<ScreenshotsManagerViewModel>(), featureFlow)
    }
    composable(AppDestination.ScreenshotsDetail.detailPattern()) { backStackEntry ->
        val parentEntry = remember(backStackEntry, navController) {
            navController.fileManagerViewModelOwnerOr(AppDestination.ScreenshotsManager, backStackEntry)
        }
        val viewModel: ScreenshotsManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        ScreenshotsFileDetailRoute(navigator, LocalPermissionCoordinator.current, viewModel, backStackEntry.initialIndex())
    }
    composable(AppDestination.VideosManager.route) {
        VideosManagerRoute(navigator, koinViewModel<VideosManagerViewModel>(), featureFlow)
    }
    composable(AppDestination.VideosDetail.detailPattern()) { backStackEntry ->
        val parentEntry = remember(backStackEntry, navController) {
            navController.fileManagerViewModelOwnerOr(AppDestination.VideosManager, backStackEntry)
        }
        val viewModel: VideosManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        VideosFileDetailRoute(navigator, LocalPermissionCoordinator.current, viewModel, backStackEntry.initialIndex())
    }
    composable(AppDestination.AudiosManager.route) {
        AudiosManagerRoute(navigator, koinViewModel<AudiosManagerViewModel>(), featureFlow)
    }
    composable(AppDestination.AudiosDetail.detailPattern()) { backStackEntry ->
        val parentEntry = remember(backStackEntry, navController) {
            navController.fileManagerViewModelOwnerOr(AppDestination.AudiosManager, backStackEntry)
        }
        val viewModel: AudiosManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        AudiosFileDetailRoute(navigator, LocalPermissionCoordinator.current, viewModel, backStackEntry.initialIndex())
    }
    composable(AppDestination.LargeFilesManager.route) {
        LargeFilesManagerRoute(navigator, koinViewModel<LargeFilesManagerViewModel>(), featureFlow)
    }
    composable(AppDestination.LargeFilesDetail.detailPattern()) { backStackEntry ->
        val parentEntry = remember(backStackEntry, navController) {
            navController.fileManagerViewModelOwnerOr(AppDestination.LargeFilesManager, backStackEntry)
        }
        val viewModel: LargeFilesManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        LargeFilesFileDetailRoute(navigator, LocalPermissionCoordinator.current, viewModel, backStackEntry.initialIndex())
    }
    composable(AppDestination.DuplicateFilesManager.route) {
        DuplicateFilesManagerRoute(navigator, koinViewModel<DuplicateFilesManagerViewModel>(), featureFlow)
    }
    composable(AppDestination.DocumentsManager.route) {
        DocumentsManagerRoute(navigator, koinViewModel<DocumentsManagerViewModel>(), featureFlow)
    }
    composable(AppDestination.DocumentsDetail.detailPattern()) { backStackEntry ->
        val parentEntry = remember(backStackEntry, navController) {
            navController.fileManagerViewModelOwnerOr(AppDestination.DocumentsManager, backStackEntry)
        }
        val viewModel: DocumentsManagerViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
        DocumentsFileDetailRoute(navigator, LocalPermissionCoordinator.current, viewModel, backStackEntry.initialIndex())
    }
}

private fun NavBackStackEntry.initialIndex(): Int =
    arguments?.getString(DETAIL_INITIAL_INDEX_ARG)?.toIntOrNull()?.coerceAtLeast(0) ?: 0

private fun NavHostController.fileManagerViewModelOwnerOr(
    destination: AppDestination,
    fallback: NavBackStackEntry,
): NavBackStackEntry =
    runCatching { getBackStackEntry(destination.route) }.getOrDefault(fallback)
