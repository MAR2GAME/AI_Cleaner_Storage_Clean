package com.aicleanerstorage.spaceclean.app.view.similarphotos.ui

import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveHome
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction


import com.aicleanerstorage.spaceclean.app.view.similarphotos.ui.SimilarPhotosManagerScreen


@Composable
fun SimilarPhotosManagerRoute(
    navigator: AppNavigator,
    viewModel: SimilarPhotosManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    SimilarPhotosManagerRouteContent(navigator = navigator, viewModel = viewModel,  permissionState = permissionState)
}

@Composable
private fun SimilarPhotosManagerRouteContent(
    navigator: AppNavigator,
    viewModel: SimilarPhotosManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val router = navigator
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val isDetailMode = displayState.detailStartIndex != null
    val displayItems = displayState.displayItems



    SimilarPhotosManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerAction.BackClicked -> viewModel.dispatch(
                    if (permissionState.granted) action else FileManagerAction.PermissionRejected,
                )
                FileManagerAction.DeleteClicked -> viewModel.dispatch(FileManagerAction.DeleteClicked)
                FileManagerAction.ContinueManagingClicked -> viewModel.dispatch(FileManagerAction.ContinueManagingClicked)
                FileManagerAction.VisibleItemsToggled -> viewModel.dispatch(FileManagerAction.VisibleItemsToggled)
                is FileManagerAction.SelectionToggled -> viewModel.dispatch(FileManagerAction.SelectionToggled(action.id))
                is FileManagerAction.GroupItemsToggled -> viewModel.dispatch(FileManagerAction.GroupToggled(action.ids))
                is FileManagerAction.DetailClicked -> {
                    viewModel.dispatch(FileManagerAction.DetailClicked(action.index))

                }
                else -> Unit
            }
        },
        onNavigate = { viewModel.dispatch(FileManagerAction.NavigationRequested(it.toAppDestination())) },
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(FileManagerAction.StopDismissed) },
        permissionState = permissionState,
        router = router,
        phase = displayState.phase,
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete && !isDetailMode,
        selectedUris = displayState.selectedUris,
        onCancelActiveOperation = { viewModel.dispatch(FileManagerAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
    )
}

