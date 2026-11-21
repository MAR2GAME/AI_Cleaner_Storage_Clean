package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.DeleteConfirmDialog
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.NoResultsDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.StopScanDialog

@Composable
fun FileManagerStopOperationDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    onQuit: () -> Unit,
    onResume: () -> Unit,
) {
    if (permissionGranted && visible) {
        StopScanDialog(
            onQuit = onQuit,
            onResume = onResume,
        )
    }
}

@Composable
fun FileManagerDeleteConfirmDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    ownerState: FileManagerPermissionState,
    onCancel: () -> Unit,
    onDeleteReady: () -> Unit,
) {
    if (permissionGranted && visible) {
        DeleteConfirmDialog(
            onCancel = onCancel,
            onDelete = { if (!ownerState.leavingPage) onDeleteReady() },
        )
    }
}

@Composable
fun FileManagerNoResultsDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    onBack: () -> Unit,
) {
    if (permissionGranted && visible) {
        NoResultsDialog(onBack = onBack)
    }
}
