package com.aicleanerstorage.spaceclean.feature.files.view.largefiles.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal object LargeFilesManagerReducer {
    fun reduce(state: LargeFilesManagerUiState, intent: FileManagerIntent): LargeFilesManagerUiState = if (intent is LargeFilesManagerStateChanged) intent.value else state
}
