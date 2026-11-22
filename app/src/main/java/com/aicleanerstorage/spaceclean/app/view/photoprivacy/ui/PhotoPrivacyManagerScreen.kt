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
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.DeleteConfirmDialog
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerBottomActionBar
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPhotoPrivacyParams
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPhotoPrivacyView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseParams
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerUiState

@Composable
fun PhotoPrivacyManagerScreen(
    state: PhotoPrivacyManagerUiState,
    onAction: (PhotoPrivacyAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onAction(PhotoPrivacyAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_photo_privacy),
        onBack = { onAction(PhotoPrivacyAction.BackClicked) },
        bottomBar = {
            if (state.permissionGranted &&
                (state.phase == FileOperationPhase.Browsing || state.phase == FileOperationPhase.NoResults)
            ) {
                FileManagerBottomActionBar(
                    enabled = state.selectedIds.isNotEmpty(),
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    text = stringResource(R.string.file_remove_location_data),
                    onClick = { onAction(PhotoPrivacyAction.DeleteClicked) },
                )
            }
        },
    ) {
        Column(modifier = modifier.background(FileManagerPageBrush).padding(horizontal = 16.dp)) {
            FileOperationPhaseContent(
            FileOperationPhaseParams(
                phase = state.phase,
                scanningText = stringResource(R.string.file_scanning_photo_privacy),
                deletingText = stringResource(R.string.file_removing_location_data_progress),
                completeText = stringResource(R.string.file_location_data_removed),
                resultAmount = state.removedLocationCount.toString(),
                resultUnit = stringResource(R.string.file_photos),
                resultCaption = stringResource(R.string.file_location_data_removed),
                onNavigateTool = { destination -> onAction(PhotoPrivacyAction.NavigationRequested(destination)) },
                onContinue = { onAction(PhotoPrivacyAction.ContinueManagingClicked) },
            
            ),
        ) {
                FileManagerPhotoPrivacyView(
                    FileManagerPhotoPrivacyParams(
                    groups = state.displayGroups,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    onToggleAll = { onAction(PhotoPrivacyAction.VisibleItemsToggled) },
                    onToggleGroup = { ids -> onAction(PhotoPrivacyAction.VisibleSelectionToggled(ids)) },
                    onSelect = { onAction(PhotoPrivacyAction.SelectionToggled(it)) },
                
                    ),
                )
            }
        }
    }
    if (state.permissionGranted && state.phase == FileOperationPhase.ConfirmDelete) {
        DeleteConfirmDialog(
            title = stringResource(R.string.file_remove_location_title),
            message = stringResource(R.string.file_remove_location_message),
            confirmText = stringResource(R.string.remove),
            onCancel = { onAction(PhotoPrivacyAction.DeleteDismissed) },
            onDelete = { onAction(PhotoPrivacyAction.DeletePrepared) },
        )
    }
}


