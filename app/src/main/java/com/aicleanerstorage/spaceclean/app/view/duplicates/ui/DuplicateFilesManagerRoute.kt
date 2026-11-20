package com.aicleanerstorage.spaceclean.app.view.duplicates.ui

import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerViewModel

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

@Composable
fun DuplicateFilesManagerRoute(
    navigator: AppNavigator,
    viewModel: DuplicateFilesManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
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
                FileManagerAction.DeleteClicked,
                FileManagerAction.GroupDeleteClicked,
                FileManagerAction.SelectAllClicked,
                FileManagerAction.AutoSelectClicked,
                FileManagerAction.WarningConfirmed,
                FileManagerAction.ContinueManagingClicked,
                is FileManagerAction.GroupClicked,
                is FileManagerAction.FileToggled,
                is FileManagerAction.GroupSelectionToggled -> viewModel.dispatch(action)
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
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete,
        onCancelActiveOperation = { viewModel.dispatch(FileManagerAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
    )
}
