package com.aicleanerstorage.spaceclean.feature.files.photoprivacy.ui

import com.aicleanerstorage.spaceclean.feature.files.shared.*


import com.aicleanerstorage.spaceclean.feature.files.photoprivacy.PhotoPrivacyManagerUiState
import com.aicleanerstorage.spaceclean.feature.files.photoprivacy.PhotoPrivacyManagerViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerBottomActionBar
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.DeleteConfirmDialog
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerPhotoPrivacyView
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerAction

@Composable
internal fun PhotoPrivacyManagerScreen(
    state: PhotoPrivacyManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onAction(FileManagerAction.Back) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_photo_privacy),
        onBack = { onAction(FileManagerAction.Back) },
        bottomBar = {
            if (permissionGranted &&
                (state.phase == FileOperationPhase.Browsing || state.phase == FileOperationPhase.NoResults)
            ) {
                FileManagerBottomActionBar(
                    enabled = state.selectedIds.isNotEmpty(),
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    text = stringResource(R.string.file_remove_location_data),
                    onClick = { onAction(FileManagerAction.RequestDelete) },
                )
            }
        },
    ) {
        Column(modifier = modifier.background(FileManagerPageBrush).padding(horizontal = 16.dp)) {
            FileOperationPhaseContent(
                phase = state.phase,
                scanningText = stringResource(R.string.file_scanning_photo_privacy),
                deletingText = stringResource(R.string.file_removing_location_data_progress),
                completeText = stringResource(R.string.file_location_data_removed),
                resultAmount = state.removedLocationCount.toString(),
                resultUnit = stringResource(R.string.file_photos),
                resultCaption = stringResource(R.string.file_location_data_removed),
                onNavigateTool = onNavigate,
                onContinue = { onAction(FileManagerAction.ContinueManaging) },
            ) {
                FileManagerPhotoPrivacyView(
                    groups = state.displayGroups,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    onToggleAll = { onAction(FileManagerAction.ToggleVisibleItems) },
                    onToggleGroup = { ids -> onAction(FileManagerAction.ToggleVisibleIds(ids)) },
                    onSelect = { onAction(FileManagerAction.ToggleSelection(it)) },
                )
            }
        }
    }
    if (permissionGranted && state.phase == FileOperationPhase.ConfirmDelete) {
        DeleteConfirmDialog(
            title = stringResource(R.string.file_remove_location_title),
            message = stringResource(R.string.file_remove_location_message),
            confirmText = stringResource(R.string.remove),
            onCancel = { onAction(FileManagerAction.CancelDelete) },
            onDelete = { onAction(FileManagerAction.DeleteReady) },
        )
    }
}
