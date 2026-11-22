package com.aicleanerstorage.spaceclean.app.view.documents.ui

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
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListDisplayStyle
import com.aicleanerstorage.spaceclean.app.view.documents.interaction.DocumentsUiState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerDeleteBottomBar
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerListView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerNoResultsContent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseParams
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseContent

@Composable
fun DocumentsManagerScreen(
    state: DocumentsUiState,
    onAction: (FilePageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollStates = remember { mutableMapOf<Int, ScrollState>() }
    val scrollState = scrollStates.getOrPut(state.selectedTabIndex) { ScrollState(0) }
    val visibleItems = state.visibleListItems
    val isDetailMode = state.detailStartIndex != null
    BackHandler { onAction(FilePageAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_documents),
        onBack = { onAction(FilePageAction.BackClicked) },
        bottomBar = {
            if (state.permissionGranted &&
                (state.phase == FileOperationPhase.Browsing || state.phase == FileOperationPhase.NoResults) &&
                !isDetailMode
            ) {
                FileManagerDeleteBottomBar(
                    enabled = state.selectedIds.isNotEmpty(),
                    selectedSizeBytes = state.selectedSizeBytes,
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    onClick = { onAction(FilePageAction.DeleteClicked) },
                )
            }
        },
    ) {
        val result = state.resultSize
        Column(modifier = modifier.background(FileManagerPageBrush).padding(horizontal = 16.dp)) {
            FileOperationPhaseContent(
            FileOperationPhaseParams(
                phase = state.phase,
                scanningText = stringResource(R.string.file_scanning_documents),
                deletingText = stringResource(R.string.file_deleting_files),
                resultAmount = result.first,
                resultUnit = result.second,
                resultCaption = stringResource(R.string.file_deleted_in_cleanup),
                onNavigateTool = { destination -> onAction(FilePageAction.NavigationRequested(destination)) },
                onContinue = { onAction(FilePageAction.ContinueManagingClicked) },
                noResultsContent = {
                    FileManagerNoResultsContent(
                        tabs = state.tabs,
                        selectedTabIndex = state.selectedTabIndex,
                        onTabSelected = { onAction(FilePageAction.TabChangeRequested(it)) },
                    )
                },
            
            ),
        ) {
                FileManagerListView(
                    tabs = state.tabs,
                    items = visibleItems,
                    selectedTabIndex = state.selectedTabIndex,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    scrollState = scrollState,
                    style = FileListDisplayStyle.Documents,
                    onTabSelected = { onAction(FilePageAction.TabChangeRequested(it)) },
                    onToggleAll = { onAction(FilePageAction.VisibleItemsToggled) },
                    onSelect = { onAction(FilePageAction.SelectionToggled(it)) },
                    onOpenDetail = { item ->
                        val index = visibleItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                        onAction(FilePageAction.DetailClicked(index))
                    },
                )
            }
        }
    }
}

