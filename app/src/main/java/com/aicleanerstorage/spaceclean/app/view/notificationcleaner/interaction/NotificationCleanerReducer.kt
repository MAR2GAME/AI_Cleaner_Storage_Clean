package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction

internal object NotificationCleanerReducer {
    fun scanCompleted(state: NotificationCleanerUiState): NotificationCleanerUiState =
        if (state.page == NotificationCleanerPage.Scanning) {
            state.copy(
                page = NotificationCleanerPage.Status,
                scanInProgress = false,
                showStopDialog = false,
                stopDialogPage = null,
            )
        } else {
            state
        }

    fun scanCancelled(state: NotificationCleanerUiState): NotificationCleanerUiState =
        if (state.page == NotificationCleanerPage.Scanning) {
            state.copy(scanInProgress = false, isLoading = false)
        } else {
            state
        }

    fun scanStarted(state: NotificationCleanerUiState): NotificationCleanerUiState =
        state.copy(
            hasAccess = true,
            page = NotificationCleanerPage.Scanning,
            isLoading = true,
            scanInProgress = true,
            isInitialized = true,
            error = null,
            showStopDialog = false,
            stopDialogPage = null,
        )

    fun notificationsCleaned(
        state: NotificationCleanerUiState,
        cleanedCount: Int,
    ): NotificationCleanerUiState =
        state.copy(
            page = NotificationCleanerPage.Cleaning,
            blockedCount = 0,
            blockedCountsByPackage = emptyMap(),
            blockedNotifications = emptyList(),
            cleanedCount = cleanedCount,
            cleaningAnimationComplete = false,
            completionResultVisible = false,
            error = null,
        )

    fun cleaningFinished(state: NotificationCleanerUiState): NotificationCleanerUiState =
        state.copy(
            page = NotificationCleanerPage.Complete,
            cleaningAnimationComplete = false,
            completionResultVisible = false,
        )

    fun completionDisplayed(
        state: NotificationCleanerUiState,
        targetPage: NotificationCleanerPage,
    ): NotificationCleanerUiState =
        when (targetPage) {
            NotificationCleanerPage.Complete ->
                state.copy(
                    page = NotificationCleanerPage.Complete,
                    cleaningAnimationComplete = false,
                    completionResultVisible = true,
                )
            else ->
                state.copy(
                    page = NotificationCleanerPage.Status,
                    cleaningAnimationComplete = false,
                    completionResultVisible = false,
                )
        }
}
