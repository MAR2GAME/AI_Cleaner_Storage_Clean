package com.aicleanerstorage.spaceclean.app.view.duplicates.ui.part

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseParams
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFileEntry
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerUiState
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateGroupItem
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

@Composable
internal fun DuplicateFilesManagerContent(
    uiState: DuplicateFilesManagerUiState,
    groupListScrollState: ScrollState,
    scrollStateForGroup: (Int) -> ScrollState,
    onToggleAll: () -> Unit,
    onOpenGroup: (DuplicateGroupItem) -> Unit,
    onToggleFile: (DuplicateFileEntry) -> Unit,
    onAutoSelect: () -> Unit,
    onToggleGroupSelection: (DuplicateGroupItem) -> Unit,
    onAcceptWarning: () -> Unit,
    onNavigateTool: (AppDestination) -> Unit,
    onContinue: () -> Unit,
) {
    val result = FormatUtil.formatSize(uiState.deletedBytes).splitFileSizeLabel()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(FileManagerPageBrush)
                .padding(horizontal = 16.dp),
    ) {
        FileOperationPhaseContent(
            FileOperationPhaseParams(
                phase = uiState.phase,
                scanningText = stringResource(R.string.file_scanning_duplicate_files),
                deletingText = stringResource(R.string.file_deleting_duplicate_files),
                resultAmount = result.first,
                resultUnit = result.second,
                resultCaption = stringResource(R.string.file_deleted_in_cleanup),
                onNavigateTool = onNavigateTool,
                onContinue = onContinue,
            ),
        ) {
            if (uiState.phase == FileOperationPhase.Browsing || uiState.phase == FileOperationPhase.ConfirmDelete) {
                val group = uiState.selectedGroup
                if (group == null) {
                    DuplicateFileGroupList(
                        groups = uiState.groups,
                        selectedFileKeys = uiState.selectedFileKeys,
                        allSelected = uiState.allSelected,
                        showWarning = uiState.showWarning,
                        scrollState = groupListScrollState,
                        onToggleAll = onToggleAll,
                        onOpenGroup = onOpenGroup,
                        onToggleGroupSelection = onToggleGroupSelection,
                        onAcceptWarning = onAcceptWarning,
                    )
                } else {
                    DuplicateFileGroupContent(
                        group = group,
                        selectedFileKeys = uiState.selectedFileKeys,
                        scrollState = scrollStateForGroup(group.id),
                        onToggleFile = onToggleFile,
                        onAutoSelect = onAutoSelect,
                        onToggleGroupSelection = { onToggleGroupSelection(group) },
                    )
                }
            }
        }
    }
}
