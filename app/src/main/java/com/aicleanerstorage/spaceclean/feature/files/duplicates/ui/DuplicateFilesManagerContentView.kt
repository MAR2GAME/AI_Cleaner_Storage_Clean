package com.aicleanerstorage.spaceclean.feature.files.duplicates.ui

import com.aicleanerstorage.spaceclean.feature.files.shared.*


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
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFileEntry
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateGroupItem
import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.feature.files.shared.splitFileSizeLabel
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFilesManagerUiState
import com.aicleanerstorage.spaceclean.common.format.FileSizeFormatter

@Composable
internal fun DuplicateFilesManagerContentView(
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
    val result = FileSizeFormatter.format(uiState.deletedBytes).splitFileSizeLabel()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FileManagerPageBrush)
            .padding(horizontal = 16.dp),
    ) {
        FileOperationPhaseContent(
            phase = uiState.phase,
            scanningText = stringResource(R.string.file_scanning_duplicate_files),
            deletingText = stringResource(R.string.file_deleting_duplicate_files),
            resultAmount = result.first,
            resultUnit = result.second,
            resultCaption = stringResource(R.string.file_deleted_in_cleanup),
            onNavigateTool = onNavigateTool,
            onContinue = onContinue,
        ) {
            if (uiState.phase == FileOperationPhase.Browsing || uiState.phase == FileOperationPhase.ConfirmDelete) {
                val group = uiState.selectedGroup
                if (group == null) {
                    DuplicateFilesGroupListView(
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
                    DuplicateFilesGroupDetailView(
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
