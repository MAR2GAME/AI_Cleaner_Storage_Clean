package com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal object SimilarPhotosManagerReducer {
    fun reduce(state: SimilarPhotosManagerUiState, intent: FileManagerIntent): SimilarPhotosManagerUiState = if (intent is SimilarPhotosManagerStateChanged) intent.value else state
}
