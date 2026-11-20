package com.aicleanerstorage.spaceclean.app.view.photoprivacy.ui

import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerViewModel

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveHome
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction


import com.aicleanerstorage.spaceclean.app.view.photoprivacy.ui.PhotoPrivacyManagerScreen


@Composable
fun PhotoPrivacyManagerRoute(
    navigator: AppNavigator,
    viewModel: PhotoPrivacyManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    PhotoPrivacyManagerRouteContent(navigator = navigator, viewModel = viewModel,  permissionState = permissionState)
}

@Composable
private fun PhotoPrivacyManagerRouteContent(
    navigator: AppNavigator,
    viewModel: PhotoPrivacyManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val router = navigator
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
                FileManagerAction.VisibleItemsToggled -> viewModel.dispatch(FileManagerAction.VisibleItemsToggled)
                FileManagerAction.DeleteClicked -> viewModel.dispatch(FileManagerAction.LocationRemovalClicked)
                FileManagerAction.DeleteDismissed -> viewModel.dispatch(FileManagerAction.LocationRemovalDismissed)
                FileManagerAction.DeletePrepared -> viewModel.dispatch(FileManagerAction.LocationRemovalConfirmed)
                FileManagerAction.ContinueManagingClicked -> viewModel.dispatch(FileManagerAction.ContinueManagingClicked)
                is FileManagerAction.SelectionToggled -> viewModel.dispatch(FileManagerAction.SelectionToggled(action.id))
                is FileManagerAction.VisibleSelectionToggled -> viewModel.dispatch(FileManagerAction.VisibleSelectionToggled(action.ids))
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
        deleteDialogVisible = false,
        selectedUris = emptyList(),
        onCancelActiveOperation = { viewModel.dispatch(FileManagerAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.LocationRemovalDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.LocationRemovalConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.LocationRemovalDismissed) },
    )

}
