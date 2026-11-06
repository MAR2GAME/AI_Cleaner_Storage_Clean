package com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation.ScreenshotsManagerUiState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal object ScreenshotsManagerReducer {
    fun reduce(state: ScreenshotsManagerUiState, intent: FileManagerIntent): ScreenshotsManagerUiState = if (intent is ScreenshotsManagerStateChanged) intent.value else state
}
