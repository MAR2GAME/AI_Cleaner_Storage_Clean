package com.aicleanerstorage.spaceclean.feature.files.view.duplicates.ui

import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateFileEntry
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateGroupItem
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateFilesManagerViewModel

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerFlowState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveBack
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow


import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.ui.DuplicateFilesManagerScreen

private val FEATURE = FeatureKey.DUPLICATE_FILES



@Composable
fun DuplicateFilesManagerRoute(
    navigator: AppNavigator,
    viewModel: DuplicateFilesManagerViewModel,
    featureFlow: FeatureFlow,
    permissionState: FileManagerPermissionState,
) {
    DuplicateFilesManagerRouteContent(navigator = navigator, viewModel = viewModel, featureFlow = featureFlow, permissionState = permissionState)
}

@Composable
private fun DuplicateFilesManagerRouteContent(
    navigator: AppNavigator,
    viewModel: DuplicateFilesManagerViewModel,
    featureFlow: FeatureFlow,
    permissionState: FileManagerPermissionState,
) {
    val router = navigator
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val selectedGroup = displayState.selectedGroup
    val context = LocalContext.current
    LaunchedEffect(uiState.skippedDeleteCount) {
        if (uiState.skippedDeleteCount > 0) {
            Toast.makeText(
                context,
                context.getString(R.string.clean_result_files_failed, uiState.skippedDeleteCount),
                Toast.LENGTH_LONG,
            ).show()
            viewModel.dispatch(FileManagerIntent.ClearSkippedDeleteCount)
        }
    }



    DuplicateFilesManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerIntent.Back -> viewModel.dispatch(
                    if (permissionState.granted) action else FileManagerIntent.PermissionRejected,
                )
                FileManagerIntent.RequestDelete -> viewModel.dispatch(FileManagerIntent.RequestDelete)
                FileManagerIntent.RequestDeleteCurrentGroup -> viewModel.dispatch(FileManagerIntent.RequestDeleteCurrentGroup)
                FileManagerIntent.ToggleAll -> viewModel.dispatch(FileManagerIntent.ToggleAll)
                FileManagerIntent.AutoSelectCurrentGroup -> viewModel.dispatch(FileManagerIntent.AutoSelectCurrentGroup)
                FileManagerIntent.AcceptWarning -> viewModel.dispatch(FileManagerIntent.AcceptWarning)
                FileManagerIntent.ContinueManaging -> viewModel.dispatch(FileManagerIntent.ContinueManaging)
                is FileManagerIntent.OpenGroup -> viewModel.dispatch(FileManagerIntent.OpenGroup(action.group))
                is FileManagerIntent.ToggleFile -> viewModel.dispatch(FileManagerIntent.ToggleFile(action.file))
                is FileManagerIntent.ToggleGroupSelection -> viewModel.dispatch(FileManagerIntent.ToggleGroupSelection(action.group))
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
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete,
        selectedUris = emptyList(),
        onCancelActiveOperation = { viewModel.dispatch(FileManagerIntent.CancelActiveOperation) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelDelete) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.RejectSystemDelete) },
    )
}
