package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileDetailUiState
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageAction
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerDetailView
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerScaffold
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerTopAction
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.fileDetailTitle

@Composable
fun FileDetailScreen(
    state: FileDetailUiState,
    onAction: (FilePageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.totalCount == 0) return

    BackHandler { onAction(FilePageAction.BackClicked) }

    FileManagerScaffold(
        title = fileDetailTitle(state.selectedCount, state.totalCount),
        onBack = { onAction(FilePageAction.BackClicked) },
        actions = {
            FileManagerTopAction(
                actionText = stringResource(R.string.file_delete_count, state.selectedCount),
                actionEnabled = state.selectedCount > 0,
                onAction = { onAction(FilePageAction.DeleteClicked) },
            )
        },
    ) {
        FileManagerDetailView(
            items = state.items,
            initialIndex = state.initialIndex,
            selectedIds = state.selectedIds,
            selectedSize = state.selectedSizeBytes,
            onToggleSelection = { onAction(FilePageAction.SelectionToggled(it)) },
        )
    }
}
