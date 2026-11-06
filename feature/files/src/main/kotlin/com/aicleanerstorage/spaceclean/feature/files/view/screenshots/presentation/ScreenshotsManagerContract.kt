package com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation.ScreenshotsManagerUiState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal data class ScreenshotsManagerStateChanged(val value: ScreenshotsManagerUiState) : FileManagerIntent
