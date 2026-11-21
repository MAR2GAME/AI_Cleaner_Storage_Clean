package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.security.model.VirusScanMode

data class VirusHomeUiState(
    val pendingScanMode: VirusScanMode? = null,
    val scanRequestPending: Boolean = false,
    val showScanNotice: Boolean = false,
    val showInstalledAppsPermissionDialog: Boolean = false,
    val waitingForSettingsReturn: Boolean = false,
)

sealed interface VirusScanAction {
    data object ResultBackClicked : VirusScanAction

    data object ResultEntered : VirusScanAction

    data object ResultLeft : VirusScanAction

    data object AdbRiskResolutionClicked : VirusScanAction

    data class AppThreatResolutionClicked(
        val packageName: String,
    ) : VirusScanAction

    data class FileThreatDeleteClicked(
        val path: String,
    ) : VirusScanAction

    data class NavigationRequested(
        val destination: AppDestination,
    ) : VirusScanAction

    data class ScanStartRequested(
        val mode: VirusScanMode,
    ) : VirusScanAction

    data object ScanCancellationRequested : VirusScanAction

    data object Entered : VirusScanAction

    data object BackClicked : VirusScanAction

    data class ScanRequested(
        val mode: VirusScanMode,
    ) : VirusScanAction

    data object ScanNoticeAccepted : VirusScanAction

    data object ScanNoticeDeclined : VirusScanAction

    data object InstalledAppsSettingsRequested : VirusScanAction

    data object InstalledAppsDialogDismissed : VirusScanAction

    data object Resumed : VirusScanAction

    data object SettingsLaunchFailed : VirusScanAction

    data class DeepScanPermissionResultReceived(
        val granted: Boolean,
    ) : VirusScanAction

    data object AdbRiskRefreshRequested : VirusScanAction

    data class PackageThreatRemovalRequested(
        val packageName: String,
    ) : VirusScanAction

    data class FileThreatRemovalRequested(
        val path: String,
    ) : VirusScanAction
}

sealed interface VirusScanEvent {
    data object NavigateBack : VirusScanEvent

    data object OpenDeveloperSettings : VirusScanEvent

    data class OpenAppSettings(
        val packageName: String,
    ) : VirusScanEvent

    data class DeleteFile(
        val path: String,
    ) : VirusScanEvent

    data class Navigate(
        val destination: AppDestination,
    ) : VirusScanEvent

    data class NavigateToResult(
        val hasThreats: Boolean,
    ) : VirusScanEvent

    data object Exit : VirusScanEvent

    data object OpenInstalledAppsSettings : VirusScanEvent

    data class RequestDeepScanPermission(
        val mode: VirusScanMode,
    ) : VirusScanEvent

    data class NavigateToScan(
        val mode: VirusScanMode,
    ) : VirusScanEvent
}
