package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction

internal object WhatsAppCleanerReducer {
    fun cleaningStarted(
        state: WhatsAppCleanerUiState,
        expectedBytes: Long,
        count: Int,
    ): WhatsAppCleanerUiState =
        state.copy(
            phase = WhatsAppCleanerPhase.Cleaning,
            selectedBytes = expectedBytes,
            selectedCount = count,
            error = null,
        )

    fun resultRequested(state: WhatsAppCleanerUiState): WhatsAppCleanerUiState =
        if (state.phase == WhatsAppCleanerPhase.CompleteAnimation) {
            state.copy(phase = WhatsAppCleanerPhase.Result)
        } else {
            state
        }

    fun cleaningCancelled(state: WhatsAppCleanerUiState): WhatsAppCleanerUiState =
        if (state.phase == WhatsAppCleanerPhase.Cleaning || state.phase == WhatsAppCleanerPhase.CompleteAnimation) {
            state.copy(phase = WhatsAppCleanerPhase.ScanResult)
        } else {
            state
        }
}
