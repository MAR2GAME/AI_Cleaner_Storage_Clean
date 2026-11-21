package com.aicleanerstorage.spaceclean.app.view.videos.ui

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.MediaManagerUiState

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerDeleteBottomBar
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerMediaGridView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerNoResultsContent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

@Composable
fun VideosManagerScreen(
    state: MediaManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollStates = remember { mutableMapOf<Int, ScrollState>() }
    val scrollState = scrollStates.getOrPut(state.selectedTabIndex) { ScrollState(0) }
    val visibleItems = state.visibleDisplayItems
    val isDetailMode = state.detailStartIndex != null
    BackHandler { onAction(FileManagerAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_videos),
        onBack = { onAction(FileManagerAction.BackClicked) },
        bottomBar = {
            if (permissionGranted &&
                (state.phase == FileOperationPhase.Browsing || state.phase == FileOperationPhase.NoResults) &&
                !isDetailMode
            ) {
                FileManagerDeleteBottomBar(
                    enabled = state.selectedIds.isNotEmpty(),
                    selectedSizeBytes = state.selectedSizeBytes,
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    onClick = { onAction(FileManagerAction.DeleteClicked) },
                )
            }
        },
    ) {
        val result = state.resultSize
        Column(modifier = modifier.background(FileManagerPageBrush).padding(horizontal = 16.dp)) {
            FileOperationPhaseContent(
                phase = state.phase,
                scanningText = stringResource(R.string.file_scanning_videos),
                deletingText = stringResource(R.string.file_deleting_files),
                resultAmount = result.first,
                resultUnit = result.second,
                resultCaption = stringResource(R.string.file_deleted_in_cleanup),
                onNavigateTool = onNavigate,
                onContinue = { onAction(FileManagerAction.ContinueManagingClicked) },
                noResultsContent = {
                    FileManagerNoResultsContent(
                        tabs = state.tabs,
                        selectedTabIndex = state.selectedTabIndex,
                        onTabSelected = { onAction(FileManagerAction.TabChangeRequested(it)) },
                    )
                },
            ) {
                FileManagerMediaGridView(
                    tabs = state.tabs,
                    items = state.displayItems,
                    selectedTabIndex = state.selectedTabIndex,
                    onTabSelected = { onAction(FileManagerAction.TabChangeRequested(it)) },
                    scrollState = scrollState,
                    selectedIds = state.selectedIds,
                    onSelect = { onAction(FileManagerAction.SelectionToggled(it)) },
                    onToggleVisibleItems = { onAction(FileManagerAction.VisibleSelectionToggled(it)) },
                    onOpenDetail = { item ->
                        val index = visibleItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                        onAction(FileManagerAction.DetailClicked(index))
                    },
                )
            }
        }
    }
}
