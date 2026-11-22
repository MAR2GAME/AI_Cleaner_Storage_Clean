package com.aicleanerstorage.spaceclean.app.root

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.aicleanerstorage.spaceclean.app.view.audios.interaction.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.audios.ui.AudiosManagerRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageAction
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.AudiosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.DocumentsFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowHost
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.LargeFilesFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.PhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.ScreenshotsFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.SimilarPhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.VideosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.documents.interaction.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.documents.ui.DocumentsManagerRoute
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesAction
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.duplicates.ui.DuplicateFilesManagerRoute
import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.largefiles.ui.LargeFilesManagerRoute
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyAction
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.ui.PhotoPrivacyManagerRoute
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photos.ui.PhotosManagerRoute
import com.aicleanerstorage.spaceclean.app.view.screenshots.interaction.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.screenshots.ui.ScreenshotsManagerRoute
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosAction
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.similarphotos.ui.SimilarPhotosManagerRoute
import com.aicleanerstorage.spaceclean.app.view.videos.interaction.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.videos.ui.VideosManagerRoute
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.fileManagerRoutes(
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
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
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
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
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
            initialLoad = SimilarPhotosAction.InitialLoadRequested,
            dismissError = SimilarPhotosAction.ErrorDismissed,
            error = { it.error },
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
            initialLoad = SimilarPhotosAction.InitialLoadRequested,
            dismissError = SimilarPhotosAction.ErrorDismissed,
            error = { it.error },
        ) { permissionState ->
            SimilarPhotosFileDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.SimilarPhotosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

// 照片隐私、重复文件是独立单页，entry 自身即流程根，同样用宿主托管流程级 effect。
    composable<AppDestination.PhotoPrivacyManager> { entry ->
        val viewModel: PhotoPrivacyManagerViewModel = koinViewModel(viewModelStoreOwner = entry)
        FileManagerFlowHost(
            rootEntry = entry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            initialLoad = PhotoPrivacyAction.InitialLoadRequested,
            dismissError = PhotoPrivacyAction.ErrorDismissed,
            error = { it.error },
            requiredPermissions = setOf(AppPermission.StorageFiles, AppPermission.MediaLocation),
            requestPermissions = false,
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
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
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
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
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
        val viewModel: VideosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
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
        val viewModel: VideosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
        ) { permissionState ->
            VideosFileDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.VideosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.AudiosManager> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.AudiosManager>() }
        val viewModel: AudiosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
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
        val viewModel: AudiosManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
        ) { permissionState ->
            AudiosFileDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.AudiosDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }

    composable<AppDestination.LargeFilesManager> { entry ->
        val rootEntry = remember(entry) { navController.getBackStackEntry<AppDestination.LargeFilesManager>() }
        val viewModel: LargeFilesManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
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
        val viewModel: LargeFilesManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
        ) { permissionState ->
            LargeFilesFileDetailRoute(
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
            initialLoad = DuplicateFilesAction.InitialLoadRequested,
            dismissError = DuplicateFilesAction.ErrorDismissed,
            error = { it.error },
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
        val viewModel: DocumentsManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
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
        val viewModel: DocumentsManagerViewModel = koinViewModel(viewModelStoreOwner = rootEntry)
        FileManagerFlowHost(
            rootEntry = rootEntry,
            currentEntry = entry,
            navigator = navigator,
            viewModel = viewModel,
            initialLoad = FilePageAction.InitialLoadRequested,
            dismissError = FilePageAction.ErrorDismissed,
            error = { it.page.error },
        ) { permissionState ->
            DocumentsFileDetailRoute(
                navigator = navigator,
                viewModel = viewModel,
                initialIndex = entry.toRoute<AppDestination.DocumentsDetail>().initialIndex.coerceAtLeast(0),
                permissionState = permissionState,
            )
        }
    }
}
