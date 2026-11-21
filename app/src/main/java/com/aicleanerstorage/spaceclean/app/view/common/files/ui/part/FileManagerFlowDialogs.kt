package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveBack
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

@Composable
fun FileManagerFlowDialogs(
    flowState: FileManagerFlowState,
    onDismissStopDialog: () -> Unit,
    permissionState: FileManagerPermissionState,
    router: AppNavigator,
    phase: FileOperationPhase,
    deleteDialogVisible: Boolean,
    onCancelActiveOperation: () -> Unit,
    onCancelDelete: () -> Unit,
    onDeleteReady: () -> Unit,
) {
    FileManagerStopOperationDialog(
        visible = flowState.showStopDialog,
        permissionGranted = permissionState.granted,
        onQuit = {
            onDismissStopDialog()
            onCancelActiveOperation()
            permissionState.leaveBack(router)
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
        onBack = { permissionState.leaveBack(router) },
    )
}
