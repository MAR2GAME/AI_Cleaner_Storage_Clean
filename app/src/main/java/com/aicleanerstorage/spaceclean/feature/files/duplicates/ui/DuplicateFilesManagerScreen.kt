package com.aicleanerstorage.spaceclean.feature.files.duplicates.ui

import com.aicleanerstorage.spaceclean.feature.files.shared.*


import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFilesManagerViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerBottomActionBar
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveBack
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.feature.files.duplicates.ui.DuplicateFilesManagerContentView
import com.aicleanerstorage.spaceclean.common.format.FileSizeFormatter
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFilesManagerUiState
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateGroupItem
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFileEntry
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFilesAction

@Composable
internal fun DuplicateFilesManagerScreen(
    state: DuplicateFilesManagerUiState,
    permissionGranted: Boolean,
    onAction: (DuplicateFilesAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupListScrollState = remember { ScrollState(0) }
    val groupDetailScrollStates = remember { mutableMapOf<Int, ScrollState>() }
    fun scrollStateForGroup(groupId: Int): ScrollState =
        groupDetailScrollStates.getOrPut(groupId) { ScrollState(0) }
    val selectedGroup = state.selectedGroup

    BackHandler { onAction(DuplicateFilesAction.Back) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_duplicate_files),
        onBack = { onAction(DuplicateFilesAction.Back) },
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
                            if (selectedGroup == null) DuplicateFilesAction.RequestDelete
                            else DuplicateFilesAction.RequestCurrentGroupDelete,
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
            onToggleAll = { onAction(DuplicateFilesAction.ToggleAll) },
            onOpenGroup = { onAction(DuplicateFilesAction.OpenGroup(it)) },
            onToggleFile = { onAction(DuplicateFilesAction.ToggleFile(it)) },
            onAutoSelect = { onAction(DuplicateFilesAction.AutoSelect) },
            onToggleGroupSelection = { onAction(DuplicateFilesAction.ToggleGroupSelection(it)) },
            onAcceptWarning = { onAction(DuplicateFilesAction.AcceptWarning) },
            onNavigateTool = onNavigate,
            onContinue = { onAction(DuplicateFilesAction.ContinueManaging) },
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
