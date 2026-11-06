package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination

import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanMode

data class VirusHomeUiState(
    val pendingScanMode: VirusScanMode? = null,
    val scanRequestPending: Boolean = false,
    val showScanNotice: Boolean = false,
    val showInstalledAppsPermissionDialog: Boolean = false,
    val waitingForSettingsReturn: Boolean = false,
)

sealed interface VirusScanIntent {
    data object ResultBack : VirusScanIntent
    data object ResultEntered : VirusScanIntent
    data object ResultLeft : VirusScanIntent
    data object SolveAdbRisk : VirusScanIntent
    data class SolveAppThreat(val packageName: String) : VirusScanIntent
    data class DeleteFileThreat(val path: String) : VirusScanIntent
    data class Navigate(val destination: AppDestination) : VirusScanIntent

    data class StartScan(val mode: VirusScanMode) : VirusScanIntent
    data object CancelScan : VirusScanIntent
    data object Entered : VirusScanIntent
    data object Back : VirusScanIntent
    data class ScanRequested(val mode: VirusScanMode) : VirusScanIntent
    data object ScanNoticeAccepted : VirusScanIntent
    data object ScanNoticeDeclined : VirusScanIntent
    data object InstalledAppsSettingsRequested : VirusScanIntent
    data object InstalledAppsDialogDismissed : VirusScanIntent
    data object Resumed : VirusScanIntent
    data object SettingsLaunchFailed : VirusScanIntent
    data class DeepScanPermissionResult(val granted: Boolean) : VirusScanIntent

    data object ResetScanState : VirusScanIntent
    data object RefreshAdbRisk : VirusScanIntent
    data object ClearError : VirusScanIntent
    data class RemoveThreatByPackage(val packageName: String) : VirusScanIntent
    data class RemoveThreatByFilePath(val path: String) : VirusScanIntent


}

sealed interface VirusScanEffect {
    data object Back : VirusScanEffect
    data object OpenDeveloperSettings : VirusScanEffect
    data class OpenAppSettings(val packageName: String) : VirusScanEffect
    data class DeleteFile(val path: String) : VirusScanEffect
    data class Navigate(val destination: AppDestination) : VirusScanEffect

    data class NavigateToResult(val hasThreats: Boolean) : VirusScanEffect
    data object Exit : VirusScanEffect
    data object OpenInstalledAppsSettings : VirusScanEffect
    data class RequestDeepScanPermission(val mode: VirusScanMode) : VirusScanEffect
    data class NavigateToScan(val mode: VirusScanMode) : VirusScanEffect
}
