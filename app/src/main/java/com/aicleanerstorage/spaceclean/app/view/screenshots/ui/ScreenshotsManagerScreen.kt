package com.aicleanerstorage.spaceclean.app.view.screenshots.ui

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
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerDeleteBottomBar
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerScreenshotGridView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.app.view.screenshots.interaction.ScreenshotsUiState

@Composable
fun ScreenshotsManagerScreen(
    state: ScreenshotsUiState,
    onAction: (FilePageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = remember { ScrollState(0) }
    val displayItems = state.displayItems
    val isDetailMode = state.detailStartIndex != null
    BackHandler { onAction(FilePageAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_screenshots),
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
                phase = state.phase,
                scanningText = stringResource(R.string.file_scanning_screenshots),
                deletingText = stringResource(R.string.file_deleting_files),
                resultAmount = result.first,
                resultUnit = result.second,
                resultCaption = stringResource(R.string.file_deleted_in_cleanup),
                onNavigateTool = { destination -> onAction(FilePageAction.NavigationRequested(destination)) },
                onContinue = { onAction(FilePageAction.ContinueManagingClicked) },
            ) {
                FileManagerScreenshotGridView(
                    items = displayItems,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    scrollState = scrollState,
                    onToggleAll = { onAction(FilePageAction.VisibleItemsToggled) },
                    onSelect = { onAction(FilePageAction.SelectionToggled(it)) },
                    onOpenDetail = { item ->
                        val index = displayItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                        onAction(FilePageAction.DetailClicked(index))
                    },
                )
            }
        }
    }
}
