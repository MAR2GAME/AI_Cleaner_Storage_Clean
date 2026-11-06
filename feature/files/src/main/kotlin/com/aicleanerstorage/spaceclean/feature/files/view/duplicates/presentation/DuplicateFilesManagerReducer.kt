package com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal object DuplicateFilesManagerReducer {
    fun reduce(state: DuplicateFilesManagerUiState, intent: FileManagerIntent): DuplicateFilesManagerUiState = if (intent is DuplicateFilesManagerStateChanged) intent.value else state
}
