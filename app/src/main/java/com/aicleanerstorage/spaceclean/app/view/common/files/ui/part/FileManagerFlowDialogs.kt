package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase

@Composable
fun FileManagerFlowDialogs(params: FileManagerFlowDialogParams) {
    FileManagerStopOperationDialog(
        visible = params.flowState.showStopDialog,
        permissionGranted = params.permissionState.granted,
        onQuit = {
            params.onDismissStopDialog()
            params.onCancelActiveOperation()
            params.onExitConfirmed()
        },
        onResume = {
            params.onDismissStopDialog()
        },
    )

    FileManagerDeleteConfirmDialog(
        visible = params.deleteDialogVisible,
        permissionGranted = params.permissionState.granted,
        ownerState = params.permissionState,
        onCancel = params.onCancelDelete,
        onDeleteReady = params.onDeleteReady,
    )

    FileManagerNoResultsDialog(
        visible = params.phase == FileOperationPhase.NoResults && !params.permissionState.leavingPage,
        permissionGranted = params.permissionState.granted,
        onBack = params.onExitConfirmed,
    )
}
