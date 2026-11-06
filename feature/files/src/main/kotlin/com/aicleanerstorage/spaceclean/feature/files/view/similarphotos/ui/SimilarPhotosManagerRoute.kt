package com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.ui

import com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.presentation.SimilarPhotosManagerViewModel

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerFlowState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerPermissionState
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
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent


import com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.ui.SimilarPhotosManagerScreen

private val FEATURE = FeatureKey.SIMILAR_PHOTOS

@Composable
fun SimilarPhotosManagerRoute(
    navigator: AppNavigator,
    viewModel: SimilarPhotosManagerViewModel,
    featureFlow: FeatureFlow,
    permissionState: FileManagerPermissionState,
) {
    SimilarPhotosManagerRouteContent(navigator = navigator, viewModel = viewModel, featureFlow = featureFlow, permissionState = permissionState)
}

@Composable
private fun SimilarPhotosManagerRouteContent(
    navigator: AppNavigator,
    viewModel: SimilarPhotosManagerViewModel,
    featureFlow: FeatureFlow,
    permissionState: FileManagerPermissionState,
) {
    val router = navigator
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val isDetailMode = displayState.detailStartIndex != null
    val displayItems = displayState.displayItems



    SimilarPhotosManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerIntent.Back -> viewModel.dispatch(
                    if (permissionState.granted) action else FileManagerIntent.PermissionRejected,
                )
                FileManagerIntent.RequestDelete -> viewModel.dispatch(FileManagerIntent.RequestDelete)
                FileManagerIntent.ContinueManaging -> viewModel.dispatch(FileManagerIntent.ContinueManaging)
                FileManagerIntent.ToggleVisibleItems -> viewModel.dispatch(FileManagerIntent.ToggleVisibleItems)
                is FileManagerIntent.ToggleSelection -> viewModel.dispatch(FileManagerIntent.ToggleSelection(action.id))
                is FileManagerIntent.ToggleGroupIds -> viewModel.dispatch(FileManagerIntent.ToggleGroup(action.ids))
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

