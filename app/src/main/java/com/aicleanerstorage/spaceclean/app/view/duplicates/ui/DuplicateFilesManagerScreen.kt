package com.aicleanerstorage.spaceclean.app.view.duplicates.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesAction
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerUiState
import com.aicleanerstorage.spaceclean.app.view.duplicates.ui.part.DuplicateFilesDeleteBottomBar
import com.aicleanerstorage.spaceclean.app.view.duplicates.ui.part.DuplicateFilesManagerContent

@Composable
fun DuplicateFilesManagerScreen(
    state: DuplicateFilesManagerUiState,
    onAction: (DuplicateFilesAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupListState = rememberLazyListState()
    val groupDetailScrollStates = remember { mutableMapOf<Int, ScrollState>() }

    fun scrollStateForGroup(groupId: Int): ScrollState = groupDetailScrollStates.getOrPut(groupId) { ScrollState(0) }

    BackHandler { onAction(DuplicateFilesAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_duplicate_files),
        onBack = { onAction(DuplicateFilesAction.BackClicked) },
        bottomBar = {
            if (state.permissionGranted &&
                (
                    state.phase == FileOperationPhase.Browsing ||
                        state.phase == FileOperationPhase.ConfirmDelete ||
                        state.phase == FileOperationPhase.NoResults
                )
            ) {
                DuplicateFilesDeleteBottomBar(state, onAction)
            }
        },
    ) {
        DuplicateFilesManagerContent(
            uiState = state,
            groupListState = groupListState,
            scrollStateForGroup = ::scrollStateForGroup,
            onToggleAll = { onAction(DuplicateFilesAction.SelectAllClicked) },
            onOpenGroup = { onAction(DuplicateFilesAction.GroupClicked(it)) },
            onToggleFile = { onAction(DuplicateFilesAction.FileToggled(it)) },
            onAutoSelect = { onAction(DuplicateFilesAction.AutoSelectClicked) },
            onToggleGroupSelection = { onAction(DuplicateFilesAction.GroupSelectionToggled(it)) },
            onAcceptWarning = { onAction(DuplicateFilesAction.WarningConfirmed) },
            onNavigateTool = { destination -> onAction(DuplicateFilesAction.NavigationRequested(destination)) },
            onContinue = { onAction(DuplicateFilesAction.ContinueManagingClicked) },
        )
    }
}
