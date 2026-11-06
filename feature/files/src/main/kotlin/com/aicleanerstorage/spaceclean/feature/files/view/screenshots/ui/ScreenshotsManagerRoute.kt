package com.aicleanerstorage.spaceclean.feature.files.view.screenshots.ui

import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation.ScreenshotsManagerViewModel

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerFlowState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import com.aicleanerstorage.spaceclean.core.navigation.AppDestination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.ui.permission.LocalPermissionGateway
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent


import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.ui.ScreenshotsManagerScreen

private val FEATURE = FeatureKey.SCREENSHOTS

@Composable
fun ScreenshotsManagerRoute(
    navigator: AppNavigator,
    viewModel: ScreenshotsManagerViewModel,
    featureFlow: FeatureFlow,
) {
    ScreenshotsManagerRouteContent(navigator = navigator, viewModel = viewModel, featureFlow = featureFlow)
}

@Composable
private fun ScreenshotsManagerRouteContent(
    navigator: AppNavigator,
    viewModel: ScreenshotsManagerViewModel,
    featureFlow: FeatureFlow,
) {
    val router = navigator
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val permissionState = rememberFileManagerPermissionState()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val isDetailMode = displayState.detailStartIndex != null
    val displayItems = displayState.displayItems



    ScreenshotsManagerScreen(
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
        permissionGateway = LocalPermissionGateway.current,
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

