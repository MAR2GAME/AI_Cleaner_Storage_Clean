package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction


import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toFileDetailDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase



import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.MediaManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.screenshots.interaction.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

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
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        dispatch = viewModel::dispatch,
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
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        dispatch = viewModel::dispatch,
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
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        dispatch = viewModel::dispatch,
    )
}

@Composable
fun MediaFileDetailRoute(
    navigator: AppNavigator,
    viewModel: MediaManagerViewModel,
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
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        dispatch = viewModel::dispatch,
    )
}

@Composable
fun FileListDetailRoute(
    navigator: AppNavigator,
    viewModel: FileListManagerViewModel,
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
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        dispatch = viewModel::dispatch,
    )
}

@Composable
private fun FileDetailRoute(
    navigator: AppNavigator,
    permissionState: FileManagerPermissionState,
    uiState: FileDetailUiState,
    dispatch: (FileManagerAction) -> Unit,
) {
    FileDetailScreen(
        state = uiState,
        onAction = { action ->
            when (action) {
                FileManagerAction.BackClicked -> dispatch(FileManagerAction.DetailBackClicked)
                FileManagerAction.DeleteClicked,
                is FileManagerAction.SelectionToggled -> dispatch(action)
                else -> Unit
            }
        },
        onNavigate = { navigator.navigate(it) },
    )

    FileManagerDeleteConfirmDialog(
        visible = uiState.confirmDeleteVisible,
        permissionGranted = permissionState.granted,
        ownerState = permissionState,
        onCancel = { dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = {
            dispatch(FileManagerAction.DeleteConfirmed)
            dispatch(FileManagerAction.DetailBackClicked)
        },
    )
}
