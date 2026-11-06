package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation

import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanUiState

internal object VirusScanReducer {
    fun reduce(state: VirusScanUiState, intent: VirusScanIntent): VirusScanUiState =
        if (intent is VirusStateChanged) intent.value else state
}
