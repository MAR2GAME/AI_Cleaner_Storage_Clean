package com.aicleanerstorage.spaceclean.app.view.duplicates.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveBack
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerViewModel
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
    val skippedMessage =
        if (uiState.skippedDeleteCount > 0) {
            stringResource(R.string.clean_result_files_failed, uiState.skippedDeleteCount)
        } else {
            null
        }
    LaunchedEffect(uiState.skippedDeleteCount, skippedMessage) {
        if (skippedMessage != null) {
            Toast.makeText(context, skippedMessage, Toast.LENGTH_LONG).show()
            viewModel.dispatch(DuplicateFilesAction.SkippedDeleteCountConsumed)
        }
    }

    DuplicateFilesManagerScreen(
        state = displayState.copy(permissionGranted = permissionState.granted),
        onAction = { action ->
            when (action) {
                DuplicateFilesAction.BackClicked ->
                    viewModel.dispatch(
                        if (permissionState.granted) action else DuplicateFilesAction.PermissionRejected,
                    )
                DuplicateFilesAction.DeleteClicked,
                DuplicateFilesAction.GroupDeleteClicked,
                DuplicateFilesAction.SelectAllClicked,
                DuplicateFilesAction.AutoSelectClicked,
                DuplicateFilesAction.WarningConfirmed,
                DuplicateFilesAction.ContinueManagingClicked,
                is DuplicateFilesAction.GroupClicked,
                is DuplicateFilesAction.FileToggled,
                is DuplicateFilesAction.GroupSelectionToggled,
                -> viewModel.dispatch(action)
                is DuplicateFilesAction.NavigationRequested -> viewModel.dispatch(action)
                else -> Unit
            }
        },
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(DuplicateFilesAction.StopDismissed) },
        permissionState = permissionState,
        onExitConfirmed = { permissionState.leaveBack(navigator) },
        phase = displayState.phase,
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete,
        onCancelActiveOperation = { viewModel.dispatch(DuplicateFilesAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(DuplicateFilesAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(DuplicateFilesAction.DeleteConfirmed) },
    )
}


