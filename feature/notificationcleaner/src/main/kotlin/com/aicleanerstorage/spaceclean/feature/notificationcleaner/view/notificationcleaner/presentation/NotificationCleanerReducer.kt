package com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation



internal object NotificationCleanerReducer {
    fun reduce(state: NotificationCleanerUiState, intent: NotificationCleanerIntent): NotificationCleanerUiState = when (intent) {
        is NotificationCleanerStateChanged -> intent.value
        else -> state
    }
}
