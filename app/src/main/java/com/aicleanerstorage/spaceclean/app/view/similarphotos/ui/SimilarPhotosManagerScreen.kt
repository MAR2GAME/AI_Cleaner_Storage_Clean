package com.aicleanerstorage.spaceclean.app.view.similarphotos.ui

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
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerDeleteBottomBar
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerSimilarPhotosView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseParams
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerUiState

@Composable
fun SimilarPhotosManagerScreen(
    state: SimilarPhotosManagerUiState,
    onAction: (SimilarPhotosAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = remember { ScrollState(0) }
    val displayItems = state.displayItems
    val isDetailMode = state.detailStartIndex != null
    BackHandler { onAction(SimilarPhotosAction.BackClicked) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_similar_photos),
        onBack = { onAction(SimilarPhotosAction.BackClicked) },
        bottomBar = {
            if (state.permissionGranted &&
                (state.phase == FileOperationPhase.Browsing || state.phase == FileOperationPhase.NoResults) &&
                !isDetailMode
            ) {
                FileManagerDeleteBottomBar(
                    enabled = state.selectedIds.isNotEmpty(),
                    selectedSizeBytes = state.selectedSizeBytes,
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    onClick = { onAction(SimilarPhotosAction.DeleteClicked) },
                )
            }
        },
    ) {
        val result = state.resultSize
        Column(modifier = modifier.background(FileManagerPageBrush).padding(horizontal = 16.dp)) {
            if (state.phase == FileOperationPhase.Browsing) {
                androidx.compose.material3.Text(stringResource(R.string.similar_photo_grouping_hint))
            }
            FileOperationPhaseContent(
            FileOperationPhaseParams(
                phase = state.phase,
                scanningText = stringResource(R.string.file_scanning_similar_photos),
                deletingText = stringResource(R.string.file_deleting_files),
                resultAmount = result.first,
                resultUnit = result.second,
                resultCaption = stringResource(R.string.file_deleted_in_cleanup),
                onNavigateTool = { destination -> onAction(SimilarPhotosAction.NavigationRequested(destination)) },
                onContinue = { onAction(SimilarPhotosAction.ContinueManagingClicked) },
            
            ),
        ) {
                FileManagerSimilarPhotosView(
                    groups = state.displayGroups,
                    totalCount = state.totalPhotoCount,
                    totalSizeBytes = state.totalSizeBytes,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    scrollState = scrollState,
                    onToggleAll = { onAction(SimilarPhotosAction.VisibleItemsToggled) },
                    onToggleGroup = { group ->
                        val bulkSelectableIds =
                            group.items
                                .filterNot { it.bestPhoto }
                                .map { it.id }
                                .toSet()
                        onAction(SimilarPhotosAction.GroupItemsToggled(bulkSelectableIds))
                    },
                    onSelect = { onAction(SimilarPhotosAction.SelectionToggled(it)) },
                    onOpenDetail = { item ->
                        val index = displayItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                        onAction(SimilarPhotosAction.DetailClicked(index))
                    },
                )
            }
        }
    }
}

