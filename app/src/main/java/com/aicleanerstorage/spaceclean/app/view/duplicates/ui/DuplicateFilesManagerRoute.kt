package com.aicleanerstorage.spaceclean.app.view.duplicates.ui

import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFileEntry
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateGroupItem
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerViewModel

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveHome
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveBack
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.toAppDestination


import com.aicleanerstorage.spaceclean.app.view.duplicates.ui.DuplicateFilesManagerScreen




@Composable
fun DuplicateFilesManagerRoute(
    navigator: AppNavigator,
    viewModel: DuplicateFilesManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    DuplicateFilesManagerRouteContent(navigator = navigator, viewModel = viewModel,  permissionState = permissionState)
}

@Composable
private fun DuplicateFilesManagerRouteContent(
    navigator: AppNavigator,
    viewModel: DuplicateFilesManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val router = navigator
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val selectedGroup = displayState.selectedGroup
    val context = LocalContext.current
    LaunchedEffect(uiState.skippedDeleteCount) {
        if (uiState.skippedDeleteCount > 0) {
            Toast.makeText(
                context,
                context.getString(R.string.clean_result_files_failed, uiState.skippedDeleteCount),
                Toast.LENGTH_LONG,
            ).show()
            viewModel.dispatch(FileManagerAction.SkippedDeleteCountConsumed)
        }
    }



    DuplicateFilesManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerAction.BackClicked -> viewModel.dispatch(
                    if (permissionState.granted) action else FileManagerAction.PermissionRejected,
                )
                FileManagerAction.DeleteClicked -> viewModel.dispatch(FileManagerAction.DeleteClicked)
                FileManagerAction.GroupDeleteClicked -> viewModel.dispatch(FileManagerAction.GroupDeleteClicked)
                FileManagerAction.SelectAllClicked -> viewModel.dispatch(FileManagerAction.SelectAllClicked)
                FileManagerAction.AutoSelectClicked -> viewModel.dispatch(FileManagerAction.AutoSelectClicked)
                FileManagerAction.WarningConfirmed -> viewModel.dispatch(FileManagerAction.WarningConfirmed)
                FileManagerAction.ContinueManagingClicked -> viewModel.dispatch(FileManagerAction.ContinueManagingClicked)
                is FileManagerAction.GroupClicked -> viewModel.dispatch(FileManagerAction.GroupClicked(action.group))
                is FileManagerAction.FileToggled -> viewModel.dispatch(FileManagerAction.FileToggled(action.file))
                is FileManagerAction.GroupSelectionToggled -> viewModel.dispatch(FileManagerAction.GroupSelectionToggled(action.group))
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
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete,
        selectedUris = emptyList(),
        onCancelActiveOperation = { viewModel.dispatch(FileManagerAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
    )
}
