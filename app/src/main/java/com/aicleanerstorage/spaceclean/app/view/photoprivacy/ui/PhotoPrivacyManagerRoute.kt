package com.aicleanerstorage.spaceclean.app.view.photoprivacy.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

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
        state = displayState.copy(permissionGranted = permissionState.granted),
        onAction = { action ->
            when (action) {
                FileManagerAction.BackClicked ->
                    viewModel.dispatch(
                        if (permissionState.granted) action else FileManagerAction.PermissionRejected,
                    )
                FileManagerAction.VisibleItemsToggled -> viewModel.dispatch(action)
                FileManagerAction.DeleteClicked -> viewModel.dispatch(FileManagerAction.LocationRemovalClicked)
                FileManagerAction.DeleteDismissed -> viewModel.dispatch(FileManagerAction.LocationRemovalDismissed)
                FileManagerAction.DeletePrepared -> viewModel.dispatch(FileManagerAction.LocationRemovalConfirmed)
                FileManagerAction.ContinueManagingClicked,
                is FileManagerAction.SelectionToggled,
                is FileManagerAction.VisibleSelectionToggled,
                -> viewModel.dispatch(action)
                is FileManagerAction.NavigationRequested -> viewModel.dispatch(action)
                else -> Unit
            }
        },
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
