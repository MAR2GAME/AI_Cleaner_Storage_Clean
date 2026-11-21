package com.aicleanerstorage.spaceclean.app.view.documents.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

@Composable
fun DocumentsManagerRoute(
    navigator: AppNavigator,
    viewModel: FileListManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState
    val latestPermissionGranted by rememberUpdatedState(permissionState.granted)
    val latestDisplayState by rememberUpdatedState(displayState)
    val latestShowStopDialog by rememberUpdatedState(flowState.showStopDialog)
    val latestLeavingPage by rememberUpdatedState(permissionState.leavingPage)
    val isDetailMode = displayState.detailStartIndex != null

    DisposableEffect(lifecycleOwner, viewModel) {
        var skipInitialResume = true
        val observer =
            LifecycleEventObserver { _, event ->
                if (event != Lifecycle.Event.ON_RESUME) return@LifecycleEventObserver
                if (skipInitialResume) {
                    skipInitialResume = false
                    return@LifecycleEventObserver
                }
                val canRefresh =
                    latestPermissionGranted &&
                        !latestLeavingPage &&
                        !latestShowStopDialog &&
                        latestDisplayState.phase == FileOperationPhase.Browsing
                if (canRefresh) viewModel.dispatch(FileManagerAction.RefreshRequested)
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DocumentsManagerScreen(
        state = displayState.copy(permissionGranted = permissionState.granted),
        onAction = { action ->
            when (action) {
                FileManagerAction.BackClicked ->
                    viewModel.dispatch(
                        if (permissionState.granted) action else FileManagerAction.PermissionRejected,
                    )
                FileManagerAction.VisibleItemsToggled,
                FileManagerAction.DeleteClicked,
                FileManagerAction.ContinueManagingClicked,
                is FileManagerAction.TabChangeRequested,
                is FileManagerAction.SelectionToggled,
                is FileManagerAction.DetailClicked,
                -> viewModel.dispatch(action)
                is FileManagerAction.NavigationRequested -> viewModel.dispatch(action)
                else -> Unit
            }
        },
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(FileManagerAction.StopDismissed) },
        permissionState = permissionState,
        router = navigator,
        phase = displayState.phase,
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete && !isDetailMode,
        onCancelActiveOperation = { viewModel.dispatch(FileManagerAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
    )
}
