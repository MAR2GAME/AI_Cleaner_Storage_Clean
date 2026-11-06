package com.aicleanerstorage.spaceclean.feature.files.view.photos.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal object PhotosManagerReducer {
    fun reduce(state: PhotosManagerUiState, intent: FileManagerIntent): PhotosManagerUiState = if (intent is PhotosManagerStateChanged) intent.value else state
}
