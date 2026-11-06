package com.aicleanerstorage.spaceclean.feature.files.view.documents.ui

import com.aicleanerstorage.spaceclean.feature.files.view.documents.presentation.DocumentsManagerViewModel

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerFlowState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import com.aicleanerstorage.spaceclean.core.navigation.AppDestination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent


import com.aicleanerstorage.spaceclean.feature.files.view.documents.ui.DocumentsManagerScreen

private val FEATURE = FeatureKey.DOCUMENTS

@Composable
fun DocumentsManagerRoute(
    navigator: AppNavigator,
    viewModel: DocumentsManagerViewModel,
    featureFlow: FeatureFlow,
) {
    DocumentsManagerRouteContent(navigator = navigator, viewModel = viewModel, featureFlow = featureFlow)
}

@Composable
private fun DocumentsManagerRouteContent(
    navigator: AppNavigator,
    viewModel: DocumentsManagerViewModel,
    featureFlow: FeatureFlow,
) {
    val router = navigator
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val permissionState = rememberFileManagerPermissionState()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val latestPermissionGranted by rememberUpdatedState(permissionState.granted)
    val latestDisplayState by rememberUpdatedState(displayState)
    val latestShowStopDialog by rememberUpdatedState(flowState.showStopDialog)
    val latestLeavingPage by rememberUpdatedState(permissionState.leavingPage)
    val isDetailMode = displayState.detailStartIndex != null
    val visibleItems = displayState.visibleDisplayItems

    DisposableEffect(lifecycleOwner, viewModel) {
        var skipInitialResume = true
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_RESUME) return@LifecycleEventObserver
            if (skipInitialResume) { skipInitialResume = false; return@LifecycleEventObserver }
            val canRefresh = latestPermissionGranted && !latestLeavingPage &&
                !latestShowStopDialog && latestDisplayState.phase == FileOperationPhase.Browsing
            if (canRefresh) viewModel.dispatch(FileManagerIntent.Refresh)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }



    DocumentsManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerIntent.Back -> viewModel.dispatch(
                    if (permissionState.granted) action else FileManagerIntent.PermissionRejected,
                )
                FileManagerIntent.ToggleVisibleItems -> viewModel.dispatch(FileManagerIntent.ToggleVisibleItems)
                FileManagerIntent.RequestDelete -> viewModel.dispatch(FileManagerIntent.RequestDelete)
                FileManagerIntent.ContinueManaging -> viewModel.dispatch(FileManagerIntent.ContinueManaging)
                is FileManagerIntent.SelectTab -> viewModel.dispatch(FileManagerIntent.SelectTab(action.index))
                is FileManagerIntent.ToggleSelection -> viewModel.dispatch(FileManagerIntent.ToggleSelection(action.id))
                is FileManagerIntent.OpenDetail -> {
                    viewModel.dispatch(FileManagerIntent.OpenDetail(action.index))

                }
                else -> Unit
            }
        },
        onNavigate = { viewModel.dispatch(FileManagerIntent.Navigate(it.toAppDestination())) },
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(FileManagerIntent.DismissStopDialog) },
        permissionState = permissionState,
        router = router,
        featureFlow = featureFlow,
        feature = FEATURE,
        phase = displayState.phase,
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete && !isDetailMode,
        selectedUris = displayState.selectedUris,
        onCancelActiveOperation = { viewModel.dispatch(FileManagerIntent.CancelActiveOperation) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelDelete) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.RejectSystemDelete) },
    )
}

