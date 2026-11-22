package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState

/** 文件页流程弹窗的展示参数与回调。 */
data class FileManagerFlowDialogParams(
    val flowState: FileManagerFlowState,
    val onDismissStopDialog: () -> Unit,
    val permissionState: FileManagerPermissionState,
    val phase: FileOperationPhase,
    val deleteDialogVisible: Boolean,
    val onCancelActiveOperation: () -> Unit,
    val onCancelDelete: () -> Unit,
    val onDeleteReady: () -> Unit,
    val onExitConfirmed: () -> Unit,
)
