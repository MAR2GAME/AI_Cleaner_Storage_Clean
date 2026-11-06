package com.aicleanerstorage.spaceclean.feature.files.view.duplicates.ui

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerBottomActionBar
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.ui.DuplicateFilesManagerContentView
import com.aicleanerstorage.spaceclean.core.contracts.format.FileSizeFormatter
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateFilesManagerUiState

@Composable
fun DuplicateFilesManagerScreen(
    state: DuplicateFilesManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerIntent) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupListScrollState = remember { ScrollState(0) }
    val groupDetailScrollStates = remember { mutableMapOf<Int, ScrollState>() }
    fun scrollStateForGroup(groupId: Int): ScrollState =
        groupDetailScrollStates.getOrPut(groupId) { ScrollState(0) }
    val selectedGroup = state.selectedGroup

    BackHandler { onAction(FileManagerIntent.Back) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_duplicate_files),
        onBack = { onAction(FileManagerIntent.Back) },
        bottomBar = {
            if (permissionGranted &&
                (state.phase == FileOperationPhase.Browsing ||
                    state.phase == FileOperationPhase.ConfirmDelete ||
                    state.phase == FileOperationPhase.NoResults)
            ) {
                val cleanupEnabled = if (selectedGroup == null) {
                    state.globalFilesToDelete.isNotEmpty()
                } else state.selectedGroupFilesToDelete.isNotEmpty()
                val cleanupSize = if (selectedGroup == null) {
                    state.selectedDeleteSize
                } else state.selectedGroupDeleteSize
                FileManagerBottomActionBar(
                    enabled = cleanupEnabled,
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    text = duplicateCleanupButtonText(cleanupSize),
                    onClick = {
                        onAction(
                            if (selectedGroup == null) FileManagerIntent.RequestDelete
                            else FileManagerIntent.RequestDeleteCurrentGroup,
                        )
                    },
                )
            }
        },
    ) {
        DuplicateFilesManagerContentView(
            uiState = state,
            groupListScrollState = groupListScrollState,
            scrollStateForGroup = ::scrollStateForGroup,
            onToggleAll = { onAction(FileManagerIntent.ToggleAll) },
            onOpenGroup = { onAction(FileManagerIntent.OpenGroup(it)) },
            onToggleFile = { onAction(FileManagerIntent.ToggleFile(it)) },
            onAutoSelect = { onAction(FileManagerIntent.AutoSelectCurrentGroup) },
            onToggleGroupSelection = { onAction(FileManagerIntent.ToggleGroupSelection(it)) },
            onAcceptWarning = { onAction(FileManagerIntent.AcceptWarning) },
            onNavigateTool = onNavigate,
            onContinue = { onAction(FileManagerIntent.ContinueManaging) },
        )
    }
}

@Composable
private fun duplicateCleanupButtonText(bytes: Long): String =
    if (bytes == 0L) {
        "Clean up 0KB"
    } else {
        stringResource(R.string.file_clean_up_size, FileSizeFormatter.format(bytes))
    }
