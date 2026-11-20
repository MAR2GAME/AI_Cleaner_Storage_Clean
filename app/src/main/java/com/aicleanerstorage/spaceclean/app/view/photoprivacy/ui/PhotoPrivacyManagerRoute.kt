package com.aicleanerstorage.spaceclean.app.view.photoprivacy.ui

import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerViewModel

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction

@Composable
fun PhotoPrivacyManagerRoute(
    navigator: AppNavigator,
    viewModel: PhotoPrivacyManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState

    PhotoPrivacyManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerAction.BackClicked -> viewModel.dispatch(
                    if (permissionState.granted) action else FileManagerAction.PermissionRejected,
                )
                FileManagerAction.VisibleItemsToggled -> viewModel.dispatch(action)
                FileManagerAction.DeleteClicked -> viewModel.dispatch(FileManagerAction.LocationRemovalClicked)
                FileManagerAction.DeleteDismissed -> viewModel.dispatch(FileManagerAction.LocationRemovalDismissed)
                FileManagerAction.DeletePrepared -> viewModel.dispatch(FileManagerAction.LocationRemovalConfirmed)
                FileManagerAction.ContinueManagingClicked,
                is FileManagerAction.SelectionToggled,
                is FileManagerAction.VisibleSelectionToggled -> viewModel.dispatch(action)
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
        deleteDialogVisible = false,
        onCancelActiveOperation = { viewModel.dispatch(FileManagerAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.LocationRemovalDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.LocationRemovalConfirmed) },
    )

}
