package com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

internal object PhotoPrivacyManagerReducer {
    fun reduce(state: PhotoPrivacyManagerUiState, intent: FileManagerIntent): PhotoPrivacyManagerUiState = if (intent is PhotoPrivacyManagerStateChanged) intent.value else state
}
