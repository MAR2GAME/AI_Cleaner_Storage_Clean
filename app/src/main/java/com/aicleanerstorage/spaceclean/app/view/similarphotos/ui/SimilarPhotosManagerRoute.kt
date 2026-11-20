package com.aicleanerstorage.spaceclean.app.view.similarphotos.ui

import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction

@Composable
fun SimilarPhotosManagerRoute(
    navigator: AppNavigator,
    viewModel: SimilarPhotosManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val isDetailMode = displayState.detailStartIndex != null

    SimilarPhotosManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerAction.BackClicked -> viewModel.dispatch(
                    if (permissionState.granted) action else FileManagerAction.PermissionRejected,
                )
                FileManagerAction.DeleteClicked,
                FileManagerAction.ContinueManagingClicked,
                FileManagerAction.VisibleItemsToggled,
                is FileManagerAction.SelectionToggled -> viewModel.dispatch(action)
                is FileManagerAction.GroupItemsToggled -> viewModel.dispatch(FileManagerAction.GroupToggled(action.ids))
                is FileManagerAction.DetailClicked -> viewModel.dispatch(action)
                else -> Unit
            }
        },
        onNavigate = { viewModel.dispatch(FileManagerAction.NavigationRequested(it)) },
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(FileManagerAction.StopDismissed) },
        permissionState = permissionState,
        router = navigator,
        phase = displayState.phase,
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete && !isDetailMode,
        onCancelActiveOperation = { viewModel.dispatch(FileManagerAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
    )
}
