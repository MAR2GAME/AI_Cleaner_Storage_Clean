package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.audios.interaction.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileDetailUiState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.toFileDetailDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerDeleteConfirmDialog
import com.aicleanerstorage.spaceclean.app.view.documents.interaction.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.screenshots.interaction.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosAction
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.videos.interaction.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

@Composable
fun PhotosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: PhotosManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FilePageDetailRoute(
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
    FilePageDetailRoute(
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
fun AudiosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: AudiosManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FilePageDetailRoute(
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
fun VideosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: VideosManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FilePageDetailRoute(
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
fun DocumentsFileDetailRoute(
    navigator: AppNavigator,
    viewModel: DocumentsManagerViewModel,
    initialIndex: Int,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FilePageDetailRoute(
        navigator = navigator,
        permissionState = permissionState,
        uiState =
            FileDetailUiState(
                items = uiState.visibleListItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        dispatch = viewModel::dispatch,
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
    FilePageDetailRoute(
        navigator = navigator,
        permissionState = permissionState,
        uiState =
            FileDetailUiState(
                items = uiState.visibleListItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        dispatch = viewModel::dispatch,
    )
}

@Composable
private fun FilePageDetailRoute(
    navigator: AppNavigator,
    permissionState: FileManagerPermissionState,
    uiState: FileDetailUiState,
    dispatch: (FilePageAction) -> Unit,
) {
    LaunchedEffect(uiState.totalCount) {
        if (uiState.totalCount == 0) dispatch(FilePageAction.DetailBackClicked)
    }
    FileDetailScreen(
        state = uiState,
        onAction = dispatch,
    )

    FileManagerDeleteConfirmDialog(
        visible = uiState.confirmDeleteVisible,
        permissionGranted = permissionState.granted,
        ownerState = permissionState,
        onCancel = { dispatch(FilePageAction.DeleteDismissed) },
        onDeleteReady = {
            dispatch(FilePageAction.DeleteConfirmed)
            dispatch(FilePageAction.DetailBackClicked)
        },
    )
}

@Composable
private fun FileDetailRoute(
    navigator: AppNavigator,
    permissionState: FileManagerPermissionState,
    uiState: FileDetailUiState,
    dispatch: (SimilarPhotosAction) -> Unit,
) {
    LaunchedEffect(uiState.totalCount) {
        if (uiState.totalCount == 0) dispatch(SimilarPhotosAction.DetailBackClicked)
    }
    FileDetailScreen(
        state = uiState,
        onAction = { action ->
            when (action) {
                FilePageAction.DetailBackClicked -> dispatch(SimilarPhotosAction.DetailBackClicked)
                FilePageAction.DeleteClicked -> dispatch(SimilarPhotosAction.DeleteClicked)
                is FilePageAction.SelectionToggled -> dispatch(SimilarPhotosAction.SelectionToggled(action.id))
                else -> Unit
            }
        },
    )

    FileManagerDeleteConfirmDialog(
        visible = uiState.confirmDeleteVisible,
        permissionGranted = permissionState.granted,
        ownerState = permissionState,
        onCancel = { dispatch(SimilarPhotosAction.DeleteDismissed) },
        onDeleteReady = {
            dispatch(SimilarPhotosAction.DeleteConfirmed)
            dispatch(SimilarPhotosAction.DetailBackClicked)
        },
    )
}
