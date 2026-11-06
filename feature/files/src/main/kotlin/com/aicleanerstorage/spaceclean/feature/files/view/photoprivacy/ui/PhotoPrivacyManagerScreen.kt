package com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.ui

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.presentation.PhotoPrivacyManagerUiState
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerBottomActionBar
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.DeleteConfirmDialog
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerPhotoPrivacyView
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

@Composable
fun PhotoPrivacyManagerScreen(
    state: PhotoPrivacyManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerIntent) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onAction(FileManagerIntent.Back) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_photo_privacy),
        onBack = { onAction(FileManagerIntent.Back) },
        bottomBar = {
            if (permissionGranted &&
                (state.phase == FileOperationPhase.Browsing || state.phase == FileOperationPhase.NoResults)
            ) {
                FileManagerBottomActionBar(
                    enabled = state.selectedIds.isNotEmpty(),
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    text = stringResource(R.string.file_remove_location_data),
                    onClick = { onAction(FileManagerIntent.RequestDelete) },
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
                onContinue = { onAction(FileManagerIntent.ContinueManaging) },
            ) {
                FileManagerPhotoPrivacyView(
                    groups = state.displayGroups,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    onToggleAll = { onAction(FileManagerIntent.ToggleVisibleItems) },
                    onToggleGroup = { ids -> onAction(FileManagerIntent.ToggleVisibleIds(ids)) },
                    onSelect = { onAction(FileManagerIntent.ToggleSelection(it)) },
                )
            }
        }
    }
    if (permissionGranted && state.phase == FileOperationPhase.ConfirmDelete) {
        DeleteConfirmDialog(
            title = stringResource(R.string.file_remove_location_title),
            message = stringResource(R.string.file_remove_location_message),
            confirmText = stringResource(R.string.remove),
            onCancel = { onAction(FileManagerIntent.CancelDelete) },
            onDelete = { onAction(FileManagerIntent.DeleteReady) },
        )
    }
}
