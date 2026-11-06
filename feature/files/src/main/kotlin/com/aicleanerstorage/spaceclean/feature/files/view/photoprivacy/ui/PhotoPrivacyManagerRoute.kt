package com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.ui

import com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.presentation.PhotoPrivacyManagerViewModel

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerFlowState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent


import com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.ui.PhotoPrivacyManagerScreen

private val FEATURE = FeatureKey.PHOTO_PRIVACY

@Composable
fun PhotoPrivacyManagerRoute(
    navigator: AppNavigator,
    viewModel: PhotoPrivacyManagerViewModel,
    featureFlow: FeatureFlow,
) {
    PhotoPrivacyManagerRouteContent(navigator = navigator, viewModel = viewModel, featureFlow = featureFlow)
}

@Composable
private fun PhotoPrivacyManagerRouteContent(
    navigator: AppNavigator,
    viewModel: PhotoPrivacyManagerViewModel,
    featureFlow: FeatureFlow,
) {
    val router = navigator
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val permissionState = rememberFileManagerPermissionState()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState



    PhotoPrivacyManagerScreen(
        state = displayState,
        permissionGranted = permissionState.granted,
        onAction = { action ->
            when (action) {
                FileManagerIntent.Back -> viewModel.dispatch(
                    if (permissionState.granted) action else FileManagerIntent.PermissionRejected,
                )
                FileManagerIntent.ToggleVisibleItems -> viewModel.dispatch(FileManagerIntent.ToggleVisibleItems)
                FileManagerIntent.RequestDelete -> viewModel.dispatch(FileManagerIntent.RequestRemoveLocation)
                FileManagerIntent.CancelDelete -> viewModel.dispatch(FileManagerIntent.CancelRemoveLocation)
                FileManagerIntent.DeleteReady -> viewModel.dispatch(FileManagerIntent.RemoveLocationData)
                FileManagerIntent.ContinueManaging -> viewModel.dispatch(FileManagerIntent.ContinueManaging)
                is FileManagerIntent.ToggleSelection -> viewModel.dispatch(FileManagerIntent.ToggleSelection(action.id))
                is FileManagerIntent.ToggleVisibleIds -> viewModel.dispatch(FileManagerIntent.ToggleVisibleIds(action.ids))
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
        deleteDialogVisible = false,
        selectedUris = emptyList(),
        onCancelActiveOperation = { viewModel.dispatch(FileManagerIntent.CancelActiveOperation) },
        onCancelDelete = { viewModel.dispatch(FileManagerIntent.CancelRemoveLocation) },
        onDeleteReady = { viewModel.dispatch(FileManagerIntent.RemoveLocationData) },
        onDeleteRejected = { viewModel.dispatch(FileManagerIntent.CancelRemoveLocation) },
    )

}
