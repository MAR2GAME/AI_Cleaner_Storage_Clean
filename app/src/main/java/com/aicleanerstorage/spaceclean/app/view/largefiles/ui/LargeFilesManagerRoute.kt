package com.aicleanerstorage.spaceclean.app.view.largefiles.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageAction
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect.FileManagerCompletionAdEffect
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveBack
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerFlowDialogParams
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

@Composable
fun LargeFilesManagerRoute(
    navigator: AppNavigator,
    viewModel: LargeFilesManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    FileManagerCompletionAdEffect(uiState.phase, AppDestination.LargeFilesManager)

    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(page = uiState.page.copy(phase = it)) } ?: uiState
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
                if (canRefresh) viewModel.dispatch(FilePageAction.RefreshRequested)
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LargeFilesManagerScreen(
        state = displayState.copy(permissionGranted = permissionState.granted),
        onAction = { action ->
            when (action) {
                FilePageAction.BackClicked ->
                    viewModel.dispatch(
                        if (permissionState.granted) action else FilePageAction.PermissionRejected,
                    )
                FilePageAction.VisibleItemsToggled,
                FilePageAction.DeleteClicked,
                FilePageAction.ContinueManagingClicked,
                is FilePageAction.TabChangeRequested,
                is FilePageAction.SelectionToggled,
                is FilePageAction.DetailClicked,
                -> viewModel.dispatch(action)
                is FilePageAction.NavigationRequested -> viewModel.dispatch(action)
                else -> Unit
            }
        },
    )

    FileManagerFlowDialogs(
        FileManagerFlowDialogParams(
            flowState = flowState,
            onDismissStopDialog = { viewModel.dispatch(FilePageAction.StopDismissed) },
            permissionState = permissionState,
            onExitConfirmed = { permissionState.leaveBack(navigator) },
            phase = displayState.phase,
            deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete && !isDetailMode,
            onCancelActiveOperation = { viewModel.dispatch(FilePageAction.OperationCancellationRequested) },
            onCancelDelete = { viewModel.dispatch(FilePageAction.DeleteDismissed) },
            onDeleteReady = { viewModel.dispatch(FilePageAction.DeleteConfirmed) },
        ),
    )
}
