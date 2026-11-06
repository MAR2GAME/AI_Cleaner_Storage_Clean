package com.aicleanerstorage.spaceclean.feature.files.view.audios.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal object AudiosManagerReducer {
    fun reduce(state: AudiosManagerUiState, intent: FileManagerIntent): AudiosManagerUiState = if (intent is AudiosManagerStateChanged) intent.value else state
}
