package com.aicleanerstorage.spaceclean.feature.files.view.shared.ui

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileDetailUiState
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerTopAction
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerDetailView



@Composable
fun FileDetailScreen(
    state: FileDetailUiState,
    onAction: (FileManagerIntent) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.totalCount) {
        if (state.totalCount == 0) onAction(FileManagerIntent.Back)
    }
    if (state.totalCount == 0) return

    BackHandler { onAction(FileManagerIntent.Back) }

    FileManagerScaffold(
        title = fileDetailTitle(state.selectedCount, state.totalCount),
        onBack = { onAction(FileManagerIntent.Back) },
        actions = {
            FileManagerTopAction(
                actionText = stringResource(R.string.file_delete_count, state.selectedCount),
                actionEnabled = state.selectedCount > 0,
                onAction = { onAction(FileManagerIntent.RequestDelete) },
            )
        },
    ) {
        FileManagerDetailView(
            items = state.items,
            initialIndex = state.initialIndex,
            selectedIds = state.selectedIds,
            selectedSize = state.selectedSizeBytes,
            onToggleSelection = { onAction(FileManagerIntent.ToggleSelection(it)) },
        )
    }
}
