package com.aicleanerstorage.spaceclean.app.view.duplicates.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerUiState
import com.aicleanerstorage.spaceclean.app.view.duplicates.ui.part.DuplicateFilesDeleteBottomBar
import com.aicleanerstorage.spaceclean.app.view.duplicates.ui.part.DuplicateFilesManagerContent
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

@Composable
fun DuplicateFilesManagerScreen(
    state: DuplicateFilesManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupListScrollState = remember { ScrollState(0) }
    val groupDetailScrollStates = remember { mutableMapOf<Int, ScrollState>() }
    fun scrollStateForGroup(groupId: Int): ScrollState =
        groupDetailScrollStates.getOrPut(groupId) { ScrollState(0) }

    BackHandler { onAction(FileManagerAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_duplicate_files),
        onBack = { onAction(FileManagerAction.BackClicked) },
        bottomBar = {
            if (permissionGranted &&
                (state.phase == FileOperationPhase.Browsing ||
                    state.phase == FileOperationPhase.ConfirmDelete ||
                    state.phase == FileOperationPhase.NoResults)
            ) {
                DuplicateFilesDeleteBottomBar(state, onAction)
            }
        },
    ) {
        DuplicateFilesManagerContent(
            uiState = state,
            groupListScrollState = groupListScrollState,
            scrollStateForGroup = ::scrollStateForGroup,
            onToggleAll = { onAction(FileManagerAction.SelectAllClicked) },
            onOpenGroup = { onAction(FileManagerAction.GroupClicked(it)) },
            onToggleFile = { onAction(FileManagerAction.FileToggled(it)) },
            onAutoSelect = { onAction(FileManagerAction.AutoSelectClicked) },
            onToggleGroupSelection = { onAction(FileManagerAction.GroupSelectionToggled(it)) },
            onAcceptWarning = { onAction(FileManagerAction.WarningConfirmed) },
            onNavigateTool = onNavigate,
            onContinue = { onAction(FileManagerAction.ContinueManagingClicked) },
        )
    }
}
