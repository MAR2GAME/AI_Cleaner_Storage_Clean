package com.aicleanerstorage.spaceclean.app.view.largefiles.ui

import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesManagerViewModel

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveHome
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase


import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction


import com.aicleanerstorage.spaceclean.app.view.largefiles.ui.LargeFilesManagerScreen


@Composable
fun LargeFilesManagerRoute(
    navigator: AppNavigator,
    viewModel: LargeFilesManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    LargeFilesManagerRouteContent(navigator = navigator, viewModel = viewModel,  permissionState = permissionState)
}

@Composable
private fun LargeFilesManagerRouteContent(
    navigator: AppNavigator,
    viewModel: LargeFilesManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val router = navigator
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
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
            if (canRefresh) viewModel.dispatch(FileManagerAction.RefreshRequested)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }



    LargeFilesManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerAction.BackClicked -> viewModel.dispatch(
                    if (permissionState.granted) action else FileManagerAction.PermissionRejected,
                )
                FileManagerAction.VisibleItemsToggled -> viewModel.dispatch(FileManagerAction.VisibleItemsToggled)
                FileManagerAction.DeleteClicked -> viewModel.dispatch(FileManagerAction.DeleteClicked)
                FileManagerAction.ContinueManagingClicked -> viewModel.dispatch(FileManagerAction.ContinueManagingClicked)
                is FileManagerAction.TabChangeRequested -> viewModel.dispatch(FileManagerAction.TabChangeRequested(action.index))
                is FileManagerAction.SelectionToggled -> viewModel.dispatch(FileManagerAction.SelectionToggled(action.id))
                is FileManagerAction.DetailClicked -> {
                    viewModel.dispatch(FileManagerAction.DetailClicked(action.index))

                }
                else -> Unit
            }
        },
        onNavigate = { viewModel.dispatch(FileManagerAction.NavigationRequested(it.toAppDestination())) },
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(FileManagerAction.StopDismissed) },
        permissionState = permissionState,
        router = router,
        phase = displayState.phase,
        deleteDialogVisible = displayState.phase == FileOperationPhase.ConfirmDelete && !isDetailMode,
        selectedUris = displayState.selectedUris,
        onCancelActiveOperation = { viewModel.dispatch(FileManagerAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(FileManagerAction.DeleteDismissed) },
        onDeleteReady = { viewModel.dispatch(FileManagerAction.DeleteConfirmed) },
        onDeleteRejected = { viewModel.dispatch(FileManagerAction.SystemDeleteRejected) },
    )
}

