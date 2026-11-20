package com.aicleanerstorage.spaceclean.app.view.largefiles.ui

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
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerListView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerNoResultsContent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesListStyle
import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesManagerUiState
import com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget

@Composable
fun LargeFilesManagerScreen(
    state: LargeFilesManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerAction) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollStates = remember { mutableMapOf<Int, ScrollState>() }
    val scrollState = scrollStates.getOrPut(state.selectedTabIndex) { ScrollState(0) }
    val visibleItems = state.visibleDisplayItems
    val isDetailMode = state.detailStartIndex != null
    BackHandler { onAction(FileManagerAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_large_files),
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
                scanningText = stringResource(R.string.file_scanning_large_files),
                deletingText = stringResource(R.string.file_deleting_files),
                resultAmount = result.first,
                resultUnit = result.second,
                resultCaption = stringResource(R.string.file_deleted_in_cleanup),
                onNavigateTool = onNavigate,
                onContinue = { onAction(FileManagerAction.ContinueManagingClicked) },
                noResultsContent = {
                    FileManagerNoResultsContent(
                        tabs = state.displayTabs,
                        selectedTabIndex = state.selectedTabIndex,
                        onTabSelected = { onAction(FileManagerAction.TabChangeRequested(it)) },
                    )
                },
            ) {
                FileManagerListView(
                    tabs = state.displayTabs,
                    items = visibleItems,
                    selectedTabIndex = state.selectedTabIndex,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    scrollState = scrollState,
                    style = LargeFilesListStyle,
                    onTabSelected = { onAction(FileManagerAction.TabChangeRequested(it)) },
                    onToggleAll = { onAction(FileManagerAction.VisibleItemsToggled) },
                    onSelect = { onAction(FileManagerAction.SelectionToggled(it)) },
                    leadingIconRes = R.drawable.ic_file_yellow,
                    onOpenDetail = { item ->
                        val index = visibleItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                        onAction(FileManagerAction.DetailClicked(index))
                    },
                )
            }
        }
    }
}
