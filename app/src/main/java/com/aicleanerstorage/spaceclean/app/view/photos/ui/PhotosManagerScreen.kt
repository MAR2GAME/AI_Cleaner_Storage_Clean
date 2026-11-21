package com.aicleanerstorage.spaceclean.app.view.photos.ui

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
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerDeleteBottomBar
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerGalleryBrowserView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerNoResultsContent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerUiState

@Composable
fun PhotosManagerScreen(
    state: PhotosManagerUiState,
    onAction: (FileManagerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollStates = remember { mutableMapOf<Int, ScrollState>() }
    val scrollState = scrollStates.getOrPut(state.selectedTabIndex) { ScrollState(0) }
    val currentItems = state.currentDisplayItems
    val isDetailMode = state.detailStartIndex != null
    BackHandler { onAction(FileManagerAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_photos),
        onBack = { onAction(FileManagerAction.BackClicked) },
        bottomBar = {
            if (state.permissionGranted &&
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
                scanningText = stringResource(R.string.file_scanning_photos),
                deletingText = stringResource(R.string.file_deleting_files),
                resultAmount = result.first,
                resultUnit = result.second,
                resultCaption = stringResource(R.string.file_deleted_in_cleanup),
                onNavigateTool = { destination -> onAction(FileManagerAction.NavigationRequested(destination)) },
                onContinue = { onAction(FileManagerAction.ContinueManagingClicked) },
                noResultsContent = {
                    FileManagerNoResultsContent(
                        tabs = state.displayTabs,
                        selectedTabIndex = state.selectedTabIndex,
                        onTabSelected = { onAction(FileManagerAction.TabChangeRequested(it)) },
                    )
                },
            ) {
                FileManagerGalleryBrowserView(
                    tabs = state.displayTabs,
                    selectedTabIndex = state.selectedTabIndex,
                    onTabSelected = { onAction(FileManagerAction.TabChangeRequested(it)) },
                    scrollState = scrollState,
                    items = currentItems,
                    selectedIds = state.selectedIds,
                    onSelect = { onAction(FileManagerAction.SelectionToggled(it)) },
                    onSelectAll = { onAction(FileManagerAction.VisibleItemsToggled) },
                    onOpenDetail = { item ->
                        val index = currentItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                        onAction(FileManagerAction.DetailClicked(index))
                    },
                )
            }
        }
    }
}
