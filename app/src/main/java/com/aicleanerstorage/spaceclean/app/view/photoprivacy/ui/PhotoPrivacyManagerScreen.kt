package com.aicleanerstorage.spaceclean.app.view.photoprivacy.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.DeleteConfirmDialog
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerBottomActionBar
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPhotoPrivacyView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerUiState
import com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget

@Composable
fun PhotoPrivacyManagerScreen(
    state: PhotoPrivacyManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerAction) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onAction(FileManagerAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_photo_privacy),
        onBack = { onAction(FileManagerAction.BackClicked) },
        bottomBar = {
            if (permissionGranted &&
                (state.phase == FileOperationPhase.Browsing || state.phase == FileOperationPhase.NoResults)
            ) {
                FileManagerBottomActionBar(
                    enabled = state.selectedIds.isNotEmpty(),
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    text = stringResource(R.string.file_remove_location_data),
                    onClick = { onAction(FileManagerAction.DeleteClicked) },
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
                onContinue = { onAction(FileManagerAction.ContinueManagingClicked) },
            ) {
                FileManagerPhotoPrivacyView(
                    groups = state.displayGroups,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    onToggleAll = { onAction(FileManagerAction.VisibleItemsToggled) },
                    onToggleGroup = { ids -> onAction(FileManagerAction.VisibleSelectionToggled(ids)) },
                    onSelect = { onAction(FileManagerAction.SelectionToggled(it)) },
                )
            }
        }
    }
    if (permissionGranted && state.phase == FileOperationPhase.ConfirmDelete) {
        DeleteConfirmDialog(
            title = stringResource(R.string.file_remove_location_title),
            message = stringResource(R.string.file_remove_location_message),
            confirmText = stringResource(R.string.remove),
            onCancel = { onAction(FileManagerAction.DeleteDismissed) },
            onDelete = { onAction(FileManagerAction.DeletePrepared) },
        )
    }
}
