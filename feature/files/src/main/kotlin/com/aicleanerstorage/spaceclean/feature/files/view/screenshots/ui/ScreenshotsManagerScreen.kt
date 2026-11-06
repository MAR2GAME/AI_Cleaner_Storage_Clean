package com.aicleanerstorage.spaceclean.feature.files.view.screenshots.ui

import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation.ScreenshotsManagerUiState


import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerDeleteBottomBar
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerScreenshotGridView
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

@Composable
fun ScreenshotsManagerScreen(
    state: ScreenshotsManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerIntent) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = remember { ScrollState(0) }
    val displayItems = state.displayItems
    val isDetailMode = state.detailStartIndex != null
    BackHandler { onAction(FileManagerIntent.Back) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_screenshots),
        onBack = { onAction(FileManagerIntent.Back) },
        bottomBar = {
            if (permissionGranted &&
                (state.phase == FileOperationPhase.Browsing || state.phase == FileOperationPhase.NoResults) &&
                !isDetailMode
            ) {
                FileManagerDeleteBottomBar(
                    enabled = state.selectedIds.isNotEmpty(),
                    selectedSizeBytes = state.selectedSizeBytes,
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    onClick = { onAction(FileManagerIntent.RequestDelete) },
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
                onNavigateTool = onNavigate,
                onContinue = { onAction(FileManagerIntent.ContinueManaging) },
            ) {
                FileManagerScreenshotGridView(
                    items = displayItems,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    scrollState = scrollState,
                    onToggleAll = { onAction(FileManagerIntent.ToggleVisibleItems) },
                    onSelect = { onAction(FileManagerIntent.ToggleSelection(it)) },
                    onOpenDetail = { item ->
                        val index = displayItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                        onAction(FileManagerIntent.OpenDetail(index))
                    },
                )
            }
        }
    }
}

