package com.aicleanerstorage.spaceclean.feature.files.duplicates

import com.aicleanerstorage.spaceclean.feature.files.shared.*


import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFilesManagerViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.height
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
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveBack
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.feature.files.duplicates.ui.DuplicateFilesManagerContentView
import com.aicleanerstorage.spaceclean.common.format.FileSizeFormatter
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFilesManagerUiState
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateGroupItem
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFileEntry


import com.aicleanerstorage.spaceclean.feature.files.duplicates.ui.DuplicateFilesManagerScreen

private val FEATURE = FeatureKey.DUPLICATE_FILES

internal sealed interface DuplicateFilesAction {
    data object Back : DuplicateFilesAction
    data object RequestDelete : DuplicateFilesAction
    data object RequestCurrentGroupDelete : DuplicateFilesAction
    data object ToggleAll : DuplicateFilesAction
    data object AutoSelect : DuplicateFilesAction
    data object AcceptWarning : DuplicateFilesAction
    data object ContinueManaging : DuplicateFilesAction
    data class OpenGroup(val group: DuplicateGroupItem) : DuplicateFilesAction
    data class ToggleFile(val file: DuplicateFileEntry) : DuplicateFilesAction
    data class ToggleGroupSelection(val group: DuplicateGroupItem) : DuplicateFilesAction
}

@Composable
internal fun DuplicateFilesManagerRoute(
    navigator: AppNavigator,
    viewModel: DuplicateFilesManagerViewModel,
    featureFlow: FeatureFlow,
) {
    DuplicateFilesManagerRouteContent(navigator = navigator, viewModel = viewModel, featureFlow = featureFlow)
}

@Composable
private fun DuplicateFilesManagerRouteContent(
    navigator: AppNavigator,
    viewModel: DuplicateFilesManagerViewModel,
    featureFlow: FeatureFlow,
) {
    val router = navigator
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionState = rememberFileManagerPermissionState()
    val flowState by viewModel.flowState.collectAsStateWithLifecycle()
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val selectedGroup = displayState.selectedGroup

    fun handleBack() {
        when {
            selectedGroup != null -> viewModel.closeGroup()
            !permissionState.granted -> permissionState.leaveBackForPermissionRejected(router, FEATURE, featureFlow)
            displayState.phase == FileOperationPhase.Deleting -> viewModel.showStopDialog()
            displayState.phase == FileOperationPhase.Scanning -> viewModel.showStopDialog(displayState.phase)
            displayState.phase == FileOperationPhase.Result -> permissionState.leaveHome(router, FEATURE, featureFlow)
            else -> permissionState.leaveBack(router, FEATURE, featureFlow)
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

    DuplicateFilesManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                DuplicateFilesAction.Back -> handleBack()
                DuplicateFilesAction.RequestDelete -> viewModel.requestDelete()
                DuplicateFilesAction.RequestCurrentGroupDelete -> viewModel.requestDeleteCurrentGroup()
                DuplicateFilesAction.ToggleAll -> viewModel.toggleAll()
                DuplicateFilesAction.AutoSelect -> viewModel.autoSelectCurrentGroup()
                DuplicateFilesAction.AcceptWarning -> viewModel.acceptWarning()
                DuplicateFilesAction.ContinueManaging -> viewModel.continueManaging()
                is DuplicateFilesAction.OpenGroup -> viewModel.openGroup(action.group)
                is DuplicateFilesAction.ToggleFile -> viewModel.toggleFile(action.file)
                is DuplicateFilesAction.ToggleGroupSelection -> viewModel.toggleGroupSelection(action.group)
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
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete,
        selectedUris = displayState.selectedUris,
        onCancelActiveOperation = viewModel::cancelActiveOperation,
        onCancelDelete = viewModel::cancelDelete,
        onDeleteReady = viewModel::deleteSelectedFiles,
        onDeleteRejected = viewModel::rejectSystemDelete,
    )
}
