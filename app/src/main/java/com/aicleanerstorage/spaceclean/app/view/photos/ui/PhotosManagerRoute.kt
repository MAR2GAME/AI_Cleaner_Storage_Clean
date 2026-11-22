package com.aicleanerstorage.spaceclean.app.view.photos.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

@Composable
fun PhotosManagerRoute(
    navigator: AppNavigator,
    viewModel: PhotosManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(page = uiState.page.copy(phase = it)) } ?: uiState
    val isDetailMode = displayState.detailStartIndex != null

    PhotosManagerScreen(
        state = displayState.copy(permissionGranted = permissionState.granted),
        onAction = { action ->
            when (action) {
                FilePageAction.BackClicked ->
                    viewModel.dispatch(
                        if (permissionState.granted) action else FilePageAction.PermissionRejected,
                    )
                FilePageAction.VisibleItemsToggled,
                FilePageAction.DeleteClicked,
                FilePageAction.ContinueManagingClicked,
                is FilePageAction.TabChangeRequested,
                is FilePageAction.SelectionToggled,
                is FilePageAction.DetailClicked,
                -> viewModel.dispatch(action)
                is FilePageAction.NavigationRequested -> viewModel.dispatch(action)
                else -> Unit
            }
        },
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(FilePageAction.StopDismissed) },
        permissionState = permissionState,
        router = navigator,
        phase = displayState.phase,
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete && !isDetailMode,
        onCancelActiveOperation = { viewModel.dispatch(FilePageAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(FilePageAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FilePageAction.DeleteConfirmed) },
    )
}
