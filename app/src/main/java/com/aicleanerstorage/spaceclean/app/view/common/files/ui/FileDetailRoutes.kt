package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction


import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toFileDetailDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase

import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileDetailScreen
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileDetailUiState

import com.aicleanerstorage.spaceclean.app.view.common.files.ui.PhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.SimilarPhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.ScreenshotsFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.VideosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.AudiosFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.LargeFilesFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.DocumentsFileDetailRoute
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerDeleteConfirmDialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.audios.interaction.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.documents.interaction.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.screenshots.interaction.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.videos.interaction.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.toAppDestination

@Composable
fun PhotosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: PhotosManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        permissionState = permissionState,
        uiState =
            FileDetailUiState(
                items = uiState.currentDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerAction.DeleteClicked) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerAction.SelectionToggled(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerAction.DetailBackClicked) },
    )
}

@Composable
fun SimilarPhotosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: SimilarPhotosManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        permissionState = permissionState,
        uiState =
            FileDetailUiState(
                items = uiState.displayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerAction.DeleteClicked) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerAction.SelectionToggled(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerAction.DetailBackClicked) },
    )
}

@Composable
fun ScreenshotsFileDetailRoute(
    navigator: AppNavigator,
    viewModel: ScreenshotsManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        permissionState = permissionState,
        uiState =
            FileDetailUiState(
                items = uiState.displayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerAction.DeleteClicked) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerAction.SelectionToggled(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerAction.DetailBackClicked) },
    )
}

@Composable
fun VideosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: VideosManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        permissionState = permissionState,
        uiState =
            FileDetailUiState(
                items = uiState.visibleDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerAction.DeleteClicked) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerAction.SelectionToggled(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerAction.DetailBackClicked) },
    )
}

@Composable
fun AudiosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: AudiosManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        permissionState = permissionState,
        uiState =
            FileDetailUiState(
                items = uiState.visibleDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerAction.DeleteClicked) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerAction.SelectionToggled(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerAction.DetailBackClicked) },
    )
}

@Composable
fun LargeFilesFileDetailRoute(
    navigator: AppNavigator,
    viewModel: LargeFilesManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        permissionState = permissionState,
        uiState =
            FileDetailUiState(
                items = uiState.visibleDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerAction.DeleteClicked) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerAction.SelectionToggled(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerAction.DetailBackClicked) },
    )
}

@Composable
fun DocumentsFileDetailRoute(
    navigator: AppNavigator,
    viewModel: DocumentsManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        permissionState = permissionState,
        uiState =
            FileDetailUiState(
                items = uiState.visibleDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerAction.DeleteClicked) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerAction.SelectionToggled(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerAction.DetailBackClicked) },
    )
}

@Composable
private fun FileDetailRoute(
    navigator: AppNavigator,
    permissionState: FileManagerPermissionState,
    uiState: FileDetailUiState,
    onRequestDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onDeleteReady: () -> Unit,
    onDeleteRejected: () -> Unit,
    onToggleSelection: (Int) -> Unit,
    onCloseDetail: () -> Unit,
) {
    val router = navigator

    fun closeAndBack() {
        onCloseDetail()
    }

    FileDetailScreen(
        state = uiState,
        onAction = { action ->
            when (action) {
                FileManagerAction.BackClicked -> closeAndBack()
                FileManagerAction.DeleteClicked -> onRequestDelete()
                FileManagerAction.DeleteDismissed -> onCancelDelete()
                FileManagerAction.DeletePrepared -> {
                    onDeleteReady()
                    closeAndBack()
                }
                FileManagerAction.SystemDeleteRejected -> onDeleteRejected()
                is FileManagerAction.SelectionToggled -> onToggleSelection(action.id)
                else -> Unit
            }
        },
        onNavigate = { navigator.navigate(it.toAppDestination()) },
    )

    FileManagerDeleteConfirmDialog(
        visible = uiState.confirmDeleteVisible,
        permissionGranted = permissionState.granted,
        selectedUris = uiState.selectedUris,
        ownerState = permissionState,
        onCancel = onCancelDelete,
        onDeleteReady = {
            onDeleteReady()
            closeAndBack()
        },
        onRejected = onDeleteRejected,
    )
}
