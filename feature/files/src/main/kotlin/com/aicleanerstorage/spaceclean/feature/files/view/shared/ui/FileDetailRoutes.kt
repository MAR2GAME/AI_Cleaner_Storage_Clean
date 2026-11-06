package com.aicleanerstorage.spaceclean.feature.files.view.shared.ui

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent


import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.toFileDetailDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase

import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileDetailScreen
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileDetailUiState

import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.PhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.SimilarPhotosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.ScreenshotsFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.VideosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.AudiosFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.LargeFilesFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.DocumentsFileDetailRoute
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerDeleteConfirmDialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.feature.files.view.audios.presentation.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.documents.presentation.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.largefiles.presentation.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.photos.presentation.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.presentation.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.videos.presentation.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination

@Composable
fun PhotosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: PhotosManagerViewModel,
    initialIndex: Int,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        uiState =
            FileDetailUiState(
                items = uiState.currentDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerIntent.RequestDelete) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelDelete) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.RejectSystemDelete) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerIntent.ToggleSelection(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerIntent.CloseDetailAndBack) },
    )
}

@Composable
fun SimilarPhotosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: SimilarPhotosManagerViewModel,
    initialIndex: Int,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        uiState =
            FileDetailUiState(
                items = uiState.displayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerIntent.RequestDelete) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelDelete) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.RejectSystemDelete) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerIntent.ToggleSelection(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerIntent.CloseDetailAndBack) },
    )
}

@Composable
fun ScreenshotsFileDetailRoute(
    navigator: AppNavigator,
    viewModel: ScreenshotsManagerViewModel,
    initialIndex: Int,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        uiState =
            FileDetailUiState(
                items = uiState.displayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerIntent.RequestDelete) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelDelete) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.RejectSystemDelete) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerIntent.ToggleSelection(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerIntent.CloseDetailAndBack) },
    )
}

@Composable
fun VideosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: VideosManagerViewModel,
    initialIndex: Int,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        uiState =
            FileDetailUiState(
                items = uiState.visibleDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerIntent.RequestDelete) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelDelete) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.RejectSystemDelete) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerIntent.ToggleSelection(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerIntent.CloseDetailAndBack) },
    )
}

@Composable
fun AudiosFileDetailRoute(
    navigator: AppNavigator,
    viewModel: AudiosManagerViewModel,
    initialIndex: Int,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        uiState =
            FileDetailUiState(
                items = uiState.visibleDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerIntent.RequestDelete) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelDelete) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.RejectSystemDelete) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerIntent.ToggleSelection(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerIntent.CloseDetailAndBack) },
    )
}

@Composable
fun LargeFilesFileDetailRoute(
    navigator: AppNavigator,
    viewModel: LargeFilesManagerViewModel,
    initialIndex: Int,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        uiState =
            FileDetailUiState(
                items = uiState.visibleDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerIntent.RequestDelete) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelDelete) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.RejectSystemDelete) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerIntent.ToggleSelection(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerIntent.CloseDetailAndBack) },
    )
}

@Composable
fun DocumentsFileDetailRoute(
    navigator: AppNavigator,
    viewModel: DocumentsManagerViewModel,
    initialIndex: Int,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileDetailRoute(
        navigator = navigator,
        uiState =
            FileDetailUiState(
                items = uiState.visibleDisplayItems.map { it.toFileDetailDisplayItem() },
                initialIndex = uiState.detailStartIndex ?: initialIndex,
                selectedIds = uiState.selectedIds,
                selectedSizeBytes = uiState.selectedSizeBytes,
                selectedUris = uiState.selectedUris,
                confirmDeleteVisible = uiState.phase == FileOperationPhase.ConfirmDelete,
            ),
        onRequestDelete = { viewModel.dispatch(FileManagerIntent.RequestDelete) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelDelete) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.RejectSystemDelete) },
        onToggleSelection = { id -> viewModel.dispatch(FileManagerIntent.ToggleSelection(id)) },
        onCloseDetail = { viewModel.dispatch(FileManagerIntent.CloseDetailAndBack) },
    )
}

@Composable
private fun FileDetailRoute(
    navigator: AppNavigator,
    uiState: FileDetailUiState,
    onRequestDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onDeleteReady: () -> Unit,
    onDeleteRejected: () -> Unit,
    onToggleSelection: (Int) -> Unit,
    onCloseDetail: () -> Unit,
) {
    val router = navigator
    val permissionState = rememberFileManagerPermissionState()

    fun closeAndBack() {
        onCloseDetail()
    }

    FileDetailScreen(
        state = uiState,
        onAction = { action ->
            when (action) {
                FileManagerIntent.Back -> closeAndBack()
                FileManagerIntent.RequestDelete -> onRequestDelete()
                FileManagerIntent.CancelDelete -> onCancelDelete()
                FileManagerIntent.DeleteReady -> {
                    onDeleteReady()
                    closeAndBack()
                }
                FileManagerIntent.RejectSystemDelete -> onDeleteRejected()
                is FileManagerIntent.ToggleSelection -> onToggleSelection(action.id)
                else -> Unit
            }
        },
        onNavigate = { navigator.navigate(it.toAppDestination()) },
    )

    FileManagerDeleteConfirmDialog(
        visible = uiState.confirmDeleteVisible,
        permissionGranted = permissionState.granted,
        selectedUris = uiState.selectedUris,
        onCancel = onCancelDelete,
        onDeleteReady = {
            onDeleteReady()
            closeAndBack()
        },
        onRejected = onDeleteRejected,
    )
}
