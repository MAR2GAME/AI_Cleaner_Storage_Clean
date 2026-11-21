package com.aicleanerstorage.spaceclean.app.view.duplicates.ui.part

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerBottomActionBar
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerUiState
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

@Composable
internal fun DuplicateFilesDeleteBottomBar(
    state: DuplicateFilesManagerUiState,
    onAction: (FileManagerAction) -> Unit,
) {
    val selectedGroup = state.selectedGroup
    val cleanupEnabled =
        if (selectedGroup == null) {
            state.globalFilesToDelete.isNotEmpty()
        } else {
            state.selectedGroupFilesToDelete.isNotEmpty()
        }
    val cleanupSize =
        if (selectedGroup == null) {
            state.selectedDeleteSize
        } else {
            state.selectedGroupDeleteSize
        }
    FileManagerBottomActionBar(
        enabled = cleanupEnabled,
        keepActiveAppearance = state.phase == FileOperationPhase.NoResults,
        text = duplicateCleanupButtonText(cleanupSize),
        onClick = {
            onAction(
                if (selectedGroup == null) {
                    FileManagerAction.DeleteClicked
                } else {
                    FileManagerAction.GroupDeleteClicked
                },
            )
        },
    )
}

@Composable
private fun duplicateCleanupButtonText(bytes: Long): String =
    if (bytes == 0L) {
        "Clean up 0KB"
    } else {
        stringResource(R.string.file_clean_up_size, FormatUtil.formatSize(bytes))
    }
