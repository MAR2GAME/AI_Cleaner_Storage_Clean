package com.aicleanerstorage.spaceclean.feature.files.shared

import com.aicleanerstorage.spaceclean.feature.files.shared.ui.*

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.feature.files.shared.BaseFileManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.shared.FileUri
import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowState
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerDeleteConfirmDialog
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerErrorToastEffect
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerOperationEventsEffect
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerStartEffect
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerStopOperationDialog
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveBack
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator

@Composable
internal fun FileManagerFlowEffects(
    viewModel: BaseFileManagerViewModel,
    featureFlow: FeatureFlow,
    permissionGateway: PermissionGateway,
    feature: FeatureKey,
    permissionState: FileManagerPermissionState,
    errorMessage: String?,
    onClearError: () -> Unit,
    onStartIfNeeded: () -> Unit,
    onPermissionRejected: () -> Unit,
    flowState: FileManagerFlowState,
) {
    FileManagerOperationEventsEffect(viewModel, featureFlow, permissionState)
    FileManagerErrorToastEffect(errorMessage, onClearError)
    FileManagerStartEffect(
        feature,
        permissionState,
        permissionGateway,
        featureFlow,
        onStartIfNeeded,
        onPermissionRejected,
    )
}

@Composable
internal fun FileManagerFlowDialogs(
    flowState: FileManagerFlowState,
    viewModel: BaseFileManagerViewModel,
    permissionState: FileManagerPermissionState,
    router: AppNavigator,
    featureFlow: FeatureFlow,
    permissionGateway: PermissionGateway,
    feature: FeatureKey,
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
            viewModel.dismissStopDialog()
            onCancelActiveOperation()
            permissionState.leaveBack(router, feature, featureFlow)
        },
        onResume = {
            viewModel.dismissStopDialog()
        },
    )

    FileManagerDeleteConfirmDialog(
        visible = deleteDialogVisible,
        permissionGranted = permissionState.granted,
        selectedUris = selectedUris,
        permissionGateway = permissionGateway,
        onCancel = onCancelDelete,
        onDeleteReady = onDeleteReady,
        onRejected = onDeleteRejected,
    )

    FileManagerNoResultsDialog(
        visible = phase == FileOperationPhase.NoResults && !permissionState.leavingPage,
        permissionGranted = permissionState.granted,
        onBack = { permissionState.leaveBack(router, feature, featureFlow) },
    )
}
