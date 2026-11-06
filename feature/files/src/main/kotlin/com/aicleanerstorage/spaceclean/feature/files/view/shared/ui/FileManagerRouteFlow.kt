package com.aicleanerstorage.spaceclean.feature.files.view.shared.ui

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerEffect

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerFlowState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase

import com.aicleanerstorage.spaceclean.core.contracts.files.FileUri

import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerOperationEventsEffect
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveBack
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerErrorToastEffect
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerStartEffect
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerStopOperationDialog
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerDeleteConfirmDialog
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerNoResultsDialog


import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import kotlinx.coroutines.flow.Flow
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator

@Composable
fun FileManagerFlowEffects(
    effects: Flow<FileManagerEffect>,
    navigator: AppNavigator,
    onCompletionAdFinished: () -> Unit,
    featureFlow: FeatureFlow,
    feature: FeatureKey,
    permissionState: FileManagerPermissionState,
    errorMessage: String?,
    onClearError: () -> Unit,
    onStartIfNeeded: () -> Unit,
    onPermissionRejected: () -> Unit,
    flowState: FileManagerFlowState,
) {
    FileManagerOperationEventsEffect(effects, onCompletionAdFinished, featureFlow, permissionState, navigator, feature)
    FileManagerErrorToastEffect(errorMessage, onClearError)
    FileManagerStartEffect(
        feature,
        permissionState,
        featureFlow,
        onStartIfNeeded,
        onPermissionRejected,
    )
}

@Composable
fun FileManagerFlowDialogs(
    flowState: FileManagerFlowState,
    onDismissStopDialog: () -> Unit,
    permissionState: FileManagerPermissionState,
    router: AppNavigator,
    featureFlow: FeatureFlow,
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
            onDismissStopDialog()
            onCancelActiveOperation()
            permissionState.leaveBack(router, feature, featureFlow)
        },
        onResume = {
            onDismissStopDialog()
        },
    )

    FileManagerDeleteConfirmDialog(
        visible = deleteDialogVisible,
        permissionGranted = permissionState.granted,
        selectedUris = selectedUris,
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
