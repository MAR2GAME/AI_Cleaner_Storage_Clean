package com.aicleanerstorage.spaceclean.app.view.similarphotos.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveBack
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

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
        state = displayState.copy(permissionGranted = permissionState.granted),
        onAction = { action ->
            when (action) {
                SimilarPhotosAction.BackClicked ->
                    viewModel.dispatch(
                        if (permissionState.granted) action else SimilarPhotosAction.PermissionRejected,
                    )
                SimilarPhotosAction.DeleteClicked,
                SimilarPhotosAction.ContinueManagingClicked,
                SimilarPhotosAction.VisibleItemsToggled,
                is SimilarPhotosAction.SelectionToggled,
                -> viewModel.dispatch(action)
                is SimilarPhotosAction.GroupItemsToggled -> viewModel.dispatch(SimilarPhotosAction.GroupToggled(action.ids))
                is SimilarPhotosAction.DetailClicked -> viewModel.dispatch(action)
                is SimilarPhotosAction.NavigationRequested -> viewModel.dispatch(action)
                else -> Unit
            }
        },
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(SimilarPhotosAction.StopDismissed) },
        permissionState = permissionState,
        onExitConfirmed = { permissionState.leaveBack(navigator) },
        phase = displayState.phase,
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete && !isDetailMode,
        onCancelActiveOperation = { viewModel.dispatch(SimilarPhotosAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(SimilarPhotosAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(SimilarPhotosAction.DeleteConfirmed) },
    )
}


