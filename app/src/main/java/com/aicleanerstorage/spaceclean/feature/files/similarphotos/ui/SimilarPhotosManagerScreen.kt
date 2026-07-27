package com.aicleanerstorage.spaceclean.feature.files.similarphotos.ui

import com.aicleanerstorage.spaceclean.feature.files.shared.*


import com.aicleanerstorage.spaceclean.app.navigation.AppDestination

import com.aicleanerstorage.spaceclean.feature.files.shared.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.similarphotos.SimilarPhotosManagerUiState
import com.aicleanerstorage.spaceclean.feature.files.similarphotos.SimilarPhotosManagerViewModel
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
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerDeleteBottomBar
import com.aicleanerstorage.spaceclean.feature.files.shared.FileImageGroupDisplayItem
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.shared.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.feature.files.shared.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileOperationPhaseContent
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerSimilarPhotosView
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerAction

@Composable
internal fun SimilarPhotosManagerScreen(
    state: SimilarPhotosManagerUiState,
    permissionGranted: Boolean,
    onAction: (FileManagerAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = remember { ScrollState(0) }
    val displayItems = state.displayItems
    val isDetailMode = state.detailStartIndex != null
    BackHandler { onAction(FileManagerAction.Back) }
    FileManagerScaffold(
        title = stringResource(R.string.nav_similar_photos),
        onBack = { onAction(FileManagerAction.Back) },
        bottomBar = {
            if (permissionGranted &&
                (state.phase == FileOperationPhase.Browsing || state.phase == FileOperationPhase.NoResults) &&
                !isDetailMode
            ) {
                FileManagerDeleteBottomBar(
                    enabled = state.selectedIds.isNotEmpty(),
                    selectedSizeBytes = state.selectedSizeBytes,
                    keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
                    onClick = { onAction(FileManagerAction.RequestDelete) },
                )
            }
        },
    ) {
        val result = state.resultSize
        Column(modifier = modifier.background(FileManagerPageBrush).padding(horizontal = 16.dp)) {
            FileOperationPhaseContent(
                phase = state.phase,
                scanningText = stringResource(R.string.file_scanning_similar_photos),
                deletingText = stringResource(R.string.file_deleting_files),
                resultAmount = result.first,
                resultUnit = result.second,
                resultCaption = stringResource(R.string.file_deleted_in_cleanup),
                onNavigateTool = onNavigate,
                onContinue = { onAction(FileManagerAction.ContinueManaging) },
            ) {
                FileManagerSimilarPhotosView(
                    groups = state.displayGroups,
                    totalCount = state.totalPhotoCount,
                    totalSizeBytes = state.totalSizeBytes,
                    selectedIds = state.selectedIds,
                    allSelected = state.allSelected,
                    scrollState = scrollState,
                    onToggleAll = { onAction(FileManagerAction.ToggleVisibleItems) },
                    onToggleGroup = { group ->
                        val bulkSelectableIds = group.items.filterNot { it.bestPhoto }.map { it.id }.toSet()
                        onAction(FileManagerAction.ToggleGroupIds(bulkSelectableIds))
                    },
                    onSelect = { onAction(FileManagerAction.ToggleSelection(it)) },
                    onOpenDetail = { item ->
                        val index = displayItems.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                        onAction(FileManagerAction.OpenDetail(index))
                    },
                )
            }
        }
    }
}

