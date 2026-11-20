package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.model.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.model.BlockedNotificationModel

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
    val blockedNotifications: List<BlockedNotificationModel> = emptyList(),
    val apps: List<BlockableNotificationAppModel> = emptyList(),
    val selectedPackages: Set<String> = emptySet(),
    val appsLoaded: Boolean = false,
    val isAppsLoading: Boolean = false,
    val page: NotificationCleanerPage = NotificationCleanerPage.Onboarding,
    val isLoading: Boolean = true,
    val scanInProgress: Boolean = false,
    val isInitialized: Boolean = false,
    val errorMessage: String? = null,
    val cleanedCount: Int = 0,
    val cleaningAnimationComplete: Boolean = false,
    val completionResultVisible: Boolean = false,
    val showStopDialog: Boolean = false,
    val stopDialogPage: NotificationCleanerPage? = null,
)

sealed interface NotificationCleanerAction {
    data class NavigationRequested(val destination: AppDestination) : NotificationCleanerAction
    data object BackClicked : NotificationCleanerAction
    data object StopClicked : NotificationCleanerAction
    data object StopConfirmed : NotificationCleanerAction
    data object StopDismissed : NotificationCleanerAction
    data object RefreshRequested : NotificationCleanerAction
    data object SettingsClicked : NotificationCleanerAction
    data object SettingsBackClicked : NotificationCleanerAction
    data object BlockingEnableRequested : NotificationCleanerAction
    data object BlockingDisableRequested : NotificationCleanerAction
    data object TidyUpClicked : NotificationCleanerAction
    data object ScanAnimationFinished : NotificationCleanerAction
    data object CleaningAnimationFinished : NotificationCleanerAction
    data object CompletionAnimationFinished : NotificationCleanerAction
    data class StatusRequested(val scanGeneration: Long) : NotificationCleanerAction
    data class PackageToggled(val packageName: String) : NotificationCleanerAction

    data object StateRefreshRequested : NotificationCleanerAction
}


sealed interface NotificationCleanerEvent {
    data object Exit : NotificationCleanerEvent
    data class Navigate(val destination: AppDestination) : NotificationCleanerEvent
}
