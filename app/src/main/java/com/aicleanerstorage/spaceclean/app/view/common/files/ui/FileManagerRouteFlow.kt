package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase

import com.aicleanerstorage.spaceclean.domain.model.FileUri

import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerOperationEventsEffect
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveBack
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerErrorToastEffect
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerStartEffect
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerStopOperationDialog
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerDeleteConfirmDialog
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerNoResultsDialog


import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

@Composable
fun FileManagerFlowEffects(
    effects: Flow<FileManagerEvent>,
    navigator: AppNavigator,
    permissionState: FileManagerPermissionState,
    errorMessage: String?,
    onClearError: () -> Unit,
    onStartIfNeeded: () -> Unit,
) {
    FileManagerOperationEventsEffect(effects, permissionState, navigator)
    FileManagerErrorToastEffect(errorMessage, onClearError)
    FileManagerStartEffect(permissionState, onStartIfNeeded)
}

@Composable
fun FileManagerFlowDialogs(
    flowState: FileManagerFlowState,
    onDismissStopDialog: () -> Unit,
    permissionState: FileManagerPermissionState,
    router: AppNavigator,
    phase: FileOperationPhase,
    deleteDialogVisible: Boolean,
    selectedUris: List<FileUri>,
    onCancelActiveOperation: () -> Unit,
    onCancelDelete: () -> Unit,
    onDeleteReady: () -> Unit,
    onDeleteRejected: () -> Unit,
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
        selectedUris = selectedUris,
        ownerState = permissionState,
        onCancel = onCancelDelete,
        onDeleteReady = onDeleteReady,
        onRejected = onDeleteRejected,
    )

    FileManagerNoResultsDialog(
        visible = phase == FileOperationPhase.NoResults && !permissionState.leavingPage,
        permissionGranted = permissionState.granted,
        onBack = { permissionState.leaveBack(router) },
    )
}
