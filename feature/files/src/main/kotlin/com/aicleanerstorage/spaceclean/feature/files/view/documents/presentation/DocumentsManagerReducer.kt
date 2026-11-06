package com.aicleanerstorage.spaceclean.feature.files.view.documents.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal object DocumentsManagerReducer {
    fun reduce(state: DocumentsManagerUiState, intent: FileManagerIntent): DocumentsManagerUiState = if (intent is DocumentsManagerStateChanged) intent.value else state
}
