package com.aicleanerstorage.spaceclean.feature.files.view.largefiles.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal data class LargeFilesManagerStateChanged(val value: LargeFilesManagerUiState) : FileManagerIntent
