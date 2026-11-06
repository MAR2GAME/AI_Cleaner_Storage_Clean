package com.aicleanerstorage.spaceclean.feature.files.view.videos.ui

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase


import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget

import com.aicleanerstorage.spaceclean.feature.files.view.videos.presentation.VideosManagerUiState
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
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerMediaGridView
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerNoResultsContent

@Composable
fun VideosManagerScreen(
    state: VideosManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerIntent) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollStates = remember { mutableMapOf<Int, ScrollState>() }
    val scrollState = scrollStates.getOrPut(state.selectedTabIndex) { ScrollState(0) }
    val visibleItems = state.visibleDisplayItems
    val isDetailMode = state.detailStartIndex != null
    BackHandler { onAction(FileManagerIntent.Back) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_videos),
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
                scanningText = stringResource(R.string.file_scanning_videos),
                deletingText = stringResource(R.string.file_deleting_files),
                resultAmount = result.first,
                resultUnit = result.second,
                resultCaption = stringResource(R.string.file_deleted_in_cleanup),
                onNavigateTool = onNavigate,
                onContinue = { onAction(FileManagerIntent.ContinueManaging) },
                noResultsContent = {
                    FileManagerNoResultsContent(
                        tabs = state.displayTabs,
                        selectedTabIndex = state.selectedTabIndex,
                        onTabSelected = { onAction(FileManagerIntent.SelectTab(it)) },
                    )
                },
            ) {
                FileManagerMediaGridView(
                    tabs = state.displayTabs,
                    items = state.displayItems,
                    selectedTabIndex = state.selectedTabIndex,
                    onTabSelected = { onAction(FileManagerIntent.SelectTab(it)) },
                    scrollState = scrollState,
                    selectedIds = state.selectedIds,
                    onSelect = { onAction(FileManagerIntent.ToggleSelection(it)) },
                    onToggleVisibleItems = { onAction(FileManagerIntent.ToggleVisibleIds(it)) },
                    onOpenDetail = { item ->
                        val index = visibleItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                        onAction(FileManagerIntent.OpenDetail(index))
                    },
                )
            }
        }
    }
}

