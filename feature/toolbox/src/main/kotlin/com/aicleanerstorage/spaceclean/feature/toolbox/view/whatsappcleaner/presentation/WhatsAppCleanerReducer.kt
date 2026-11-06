package com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.presentation



internal object WhatsAppCleanerReducer {
    fun reduce(state: WhatsAppCleanerUiState, intent: WhatsAppCleanerIntent): WhatsAppCleanerUiState = when (intent) {
        is WhatsAppCleanerStateChanged -> intent.value
        else -> state
    }
}

