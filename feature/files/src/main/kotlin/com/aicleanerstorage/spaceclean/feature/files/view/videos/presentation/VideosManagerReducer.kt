package com.aicleanerstorage.spaceclean.feature.files.view.videos.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal object VideosManagerReducer {
    fun reduce(state: VideosManagerUiState, intent: FileManagerIntent): VideosManagerUiState = if (intent is VideosManagerStateChanged) intent.value else state
}
