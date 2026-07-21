package com.aicleanerstorage.spaceclean.feature.files.screenshots

import com.aicleanerstorage.spaceclean.feature.files.shared.*


import com.aicleanerstorage.spaceclean.app.navigation.AppDestination

import com.aicleanerstorage.spaceclean.feature.files.shared.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.screenshots.ScreenshotsManagerUiState
import com.aicleanerstorage.spaceclean.feature.files.screenshots.ScreenshotsManagerViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.feature.FeatureKey
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.runtime.featureflow.FeatureFlowRuntime
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerTopAction
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerScreenshotGridView
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerAction


import com.aicleanerstorage.spaceclean.feature.files.screenshots.ui.ScreenshotsManagerScreen

private val FEATURE = FeatureKey.SCREENSHOTS

@Composable
internal fun ScreenshotsManagerRoute(
    navigator: AppNavigator,
    viewModel: ScreenshotsManagerViewModel,
    featureFlow: FeatureFlowRuntime,
) {
    ScreenshotsManagerRouteContent(navigator = navigator, viewModel = viewModel, featureFlow = featureFlow)
}

@Composable
private fun ScreenshotsManagerRouteContent(
    navigator: AppNavigator,
    viewModel: ScreenshotsManagerViewModel,
    featureFlow: FeatureFlowRuntime,
) {
    val router = navigator
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionState = rememberFileManagerPermissionState()
    val flowState by viewModel.flowState.collectAsStateWithLifecycle()
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val isDetailMode = displayState.detailStartIndex != null
    val displayItems = displayState.displayItems

    fun handleBack() {
        when {
            isDetailMode -> viewModel.closeDetail()
            !permissionState.granted -> permissionState.leaveBackForPermissionRejected(router, FEATURE, featureFlow)
            displayState.phase == FileOperationPhase.Deleting -> viewModel.showStopDialog()
            displayState.phase == FileOperationPhase.Scanning -> viewModel.showStopDialog(displayState.phase)
            displayState.phase == FileOperationPhase.ConfirmDelete -> viewModel.cancelDelete()
            else -> permissionState.leaveHome(router, FEATURE, featureFlow)
        }
    }

    FileManagerFlowEffects(
        viewModel = viewModel,
        featureFlow = featureFlow,
        permissionCoordinator = LocalPermissionCoordinator.current,
        feature = FEATURE,
        permissionState = permissionState,
        errorMessage = uiState.errorMessage,
        onClearError = viewModel::clearError,
        onStartIfNeeded = viewModel::startIfNeeded,
        onPermissionRejected = { permissionState.leaveHome(router) },
        flowState = flowState,
    )

    ScreenshotsManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerAction.Back -> handleBack()
                FileManagerAction.ToggleVisibleItems -> viewModel.toggleVisibleItems()
                FileManagerAction.RequestDelete -> viewModel.requestDelete()
                FileManagerAction.ContinueManaging -> viewModel.continueManaging()
                is FileManagerAction.ToggleSelection -> viewModel.toggleSelection(action.id)
                is FileManagerAction.OpenDetail -> {
                    viewModel.openDetail(action.index)
                    router.openRoute(AppDestination.ScreenshotsDetail.withDetailInitialIndex(action.index))
                }
                else -> Unit
            }
        },
        onNavigate = router::resetTo,
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        viewModel = viewModel,
        permissionState = permissionState,
        router = router,
        featureFlow = featureFlow,
        permissionCoordinator = LocalPermissionCoordinator.current,
        feature = FEATURE,
        phase = displayState.phase,
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete && !isDetailMode,
        selectedUris = displayState.selectedUris,
        onCancelActiveOperation = viewModel::cancelActiveOperation,
        onCancelDelete = viewModel::cancelDelete,
        onDeleteReady = viewModel::deleteSelectedFiles,
        onDeleteRejected = viewModel::rejectSystemDelete,
    )
}

