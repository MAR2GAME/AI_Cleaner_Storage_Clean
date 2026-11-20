package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileDetailUiState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerDetailView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerTopAction
import com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget



@Composable
fun FileDetailScreen(
    state: FileDetailUiState,
    onAction: (FileManagerAction) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.totalCount) {
        if (state.totalCount == 0) onAction(FileManagerAction.BackClicked)
    }
    if (state.totalCount == 0) return

    BackHandler { onAction(FileManagerAction.BackClicked) }

    FileManagerScaffold(
        title = fileDetailTitle(state.selectedCount, state.totalCount),
        onBack = { onAction(FileManagerAction.BackClicked) },
        actions = {
            FileManagerTopAction(
                actionText = stringResource(R.string.file_delete_count, state.selectedCount),
                actionEnabled = state.selectedCount > 0,
                onAction = { onAction(FileManagerAction.DeleteClicked) },
            )
        },
    ) {
        FileManagerDetailView(
            items = state.items,
            initialIndex = state.initialIndex,
            selectedIds = state.selectedIds,
            selectedSize = state.selectedSizeBytes,
            onToggleSelection = { onAction(FileManagerAction.SelectionToggled(it)) },
        )
    }
}
