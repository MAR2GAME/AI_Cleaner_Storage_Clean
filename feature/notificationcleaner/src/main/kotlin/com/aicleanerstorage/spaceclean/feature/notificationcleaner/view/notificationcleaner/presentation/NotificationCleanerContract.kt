package com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.domain.BlockableNotificationApp
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.domain.BlockedNotification
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent

enum class NotificationCleanerPage {
    Onboarding,
    Scanning,
    Status,
    Settings,
    Cleaning,
    Complete,
}

data class NotificationCleanerUiState(
    val hasAccess: Boolean = false,
    val enabled: Boolean = false,
    val blockedCount: Int = 0,
    val blockedCountsByPackage: Map<String, Int> = emptyMap(),
    val blockedNotifications: List<BlockedNotification> = emptyList(),
    val apps: List<BlockableNotificationApp> = emptyList(),
    val selectedPackages: Set<String> = emptySet(),
    val appsLoaded: Boolean = false,
    val isAppsLoading: Boolean = false,
    val page: NotificationCleanerPage = NotificationCleanerPage.Onboarding,
    val isLoading: Boolean = true,
    val scanInProgress: Boolean = false,
    val isInitialized: Boolean = false,
    val errorMessage: String? = null,
    val completionAdInFlight: Boolean = false,
    val cleanedCount: Int = 0,
    val cleaningAnimationComplete: Boolean = false,
    val completionResultVisible: Boolean = false,
    val showStopDialog: Boolean = false,
    val stopDialogPage: NotificationCleanerPage? = null,
)

sealed interface NotificationCleanerIntent {
    data class Navigate(val destination: AppDestination) : NotificationCleanerIntent
    data object Back : NotificationCleanerIntent
    data object RequestStop : NotificationCleanerIntent
    data object QuitStoppedOperation : NotificationCleanerIntent
    data object ResumeStoppedOperation : NotificationCleanerIntent
    data object Refresh : NotificationCleanerIntent
    data object ShowSettings : NotificationCleanerIntent
    data object LeaveSettings : NotificationCleanerIntent
    data object EnableBlocking : NotificationCleanerIntent
    data object DisableBlocking : NotificationCleanerIntent
    data object TidyUp : NotificationCleanerIntent
    data object FinishScanning : NotificationCleanerIntent
    data object FinishCleaning : NotificationCleanerIntent
    data object FinishCompleteAnimation : NotificationCleanerIntent
    data object ShowStatusAfterCompletionAd : NotificationCleanerIntent
    data object CompletionAdStarted : NotificationCleanerIntent
    data object CompletionAdFinished : NotificationCleanerIntent
    data class ShowStatus(val scanGeneration: Long) : NotificationCleanerIntent
    data class TogglePackage(val packageName: String) : NotificationCleanerIntent

    data object RefreshState : NotificationCleanerIntent
}

data class NotificationCleanerOperationEvent(
    val event: FeatureOperationEvent,
    val scanGeneration: Long? = null,
) : NotificationCleanerEffect

internal data class NotificationCleanerStateChanged(val value: NotificationCleanerUiState) : NotificationCleanerIntent

sealed interface NotificationCleanerEffect {
    data object Exit : NotificationCleanerEffect
    data class Navigate(val destination: AppDestination) : NotificationCleanerEffect
}
