package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState

@Composable
fun FileManagerFlowDialogs(
    flowState: FileManagerFlowState,
    onDismissStopDialog: () -> Unit,
    permissionState: FileManagerPermissionState,
    phase: FileOperationPhase,
    deleteDialogVisible: Boolean,
    onCancelActiveOperation: () -> Unit,
    onCancelDelete: () -> Unit,
    onDeleteReady: () -> Unit,
    onExitConfirmed: () -> Unit,
) {
    FileManagerStopOperationDialog(
        visible = flowState.showStopDialog,
        permissionGranted = permissionState.granted,
        onQuit = {
            onDismissStopDialog()
            onCancelActiveOperation()
            onExitConfirmed()
        },
        onResume = {
            onDismissStopDialog()
        },
    )

    FileManagerDeleteConfirmDialog(
        visible = deleteDialogVisible,
        permissionGranted = permissionState.granted,
        ownerState = permissionState,
        onCancel = onCancelDelete,
        onDeleteReady = onDeleteReady,
    )

    FileManagerNoResultsDialog(
        visible = phase == FileOperationPhase.NoResults && !permissionState.leavingPage,
        permissionGranted = permissionState.granted,
        onBack = onExitConfirmed,
    )
}
