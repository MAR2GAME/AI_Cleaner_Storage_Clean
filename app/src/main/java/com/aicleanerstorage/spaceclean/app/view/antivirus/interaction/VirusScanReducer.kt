package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import com.aicleanerstorage.spaceclean.domain.model.VirusThreatModel

internal object VirusScanReducer {
    fun reduce(state: VirusScanUiState, action: VirusScanAction): VirusScanUiState =
        when (action) {
            is VirusScanUpdate -> reduceUpdate(state, action)
            is VirusScanAction.ScanRequested -> if (state.home.scanRequestPending) state
                else state.copy(home = state.home.copy(pendingScanMode = action.mode))
            VirusScanAction.ScanNoticeAccepted, VirusScanAction.ScanNoticeDeclined ->
                state.copy(home = state.home.copy(showScanNotice = false))
            VirusScanAction.InstalledAppsSettingsRequested ->
                state.copy(home = state.home.copy(showInstalledAppsPermissionDialog = false, waitingForSettingsReturn = true))
            VirusScanAction.InstalledAppsDialogDismissed ->
                state.copy(home = state.home.copy(showInstalledAppsPermissionDialog = false, scanRequestPending = false))
            VirusScanAction.SettingsLaunchFailed ->
                state.copy(home = state.home.copy(waitingForSettingsReturn = false, scanRequestPending = false, showInstalledAppsPermissionDialog = true))
            is VirusScanAction.DeepScanPermissionResultReceived ->
                state.copy(home = state.home.copy(scanRequestPending = false))
            VirusScanAction.ErrorDismissed -> state.copy(errorMessage = null)
            VirusScanAction.ScanCancellationRequested -> state.copy(isScanning = false, scanCompleted = false)
            is VirusScanAction.PackageThreatRemovalRequested ->
                state.withThreats(state.threats.filterNot { it.packageName == action.packageName })
            is VirusScanAction.FileThreatRemovalRequested ->
                state.withThreats(state.threats.filterNot { it.apkPath == action.path })
            else -> state
        }

    private fun reduceUpdate(state: VirusScanUiState, update: VirusScanUpdate): VirusScanUiState =
        when (update) {
            VirusScanUpdate.ScanNoticeShown -> state.copy(home = state.home.copy(showScanNotice = true))
            is VirusScanUpdate.AccessCheckStarted -> state.copy(home = state.home.copy(
                pendingScanMode = update.mode, scanRequestPending = true, waitingForSettingsReturn = false,
            ))
            is VirusScanUpdate.AccessCheckFinished -> state.copy(home = state.home.copy(
                scanRequestPending = false, waitingForSettingsReturn = false,
                showInstalledAppsPermissionDialog = update.showPermissionDialog,
            ))
            VirusScanUpdate.ScanReset -> VirusScanUiState(home = state.home)
            is VirusScanUpdate.ScanStarted -> state.copy(mode = update.mode, isScanning = true, currentIcon = state.currentIcon ?: "@protection")
            is VirusScanUpdate.AdbRiskChecked -> state.copy(hasAdbRisk = update.hasRisk)
            is VirusScanUpdate.ThreatFound -> state.withThreats(state.threats + update.threat)
            is VirusScanUpdate.ProgressUpdated -> if (state.isScanning)
                state.copy(progressFraction = maxOf(state.progressFraction, update.fraction)) else state
            is VirusScanUpdate.AppDisplayed -> if (state.isScanning && !state.isPathMode)
                state.copy(currentLabel = update.label, currentIcon = update.icon) else state
            VirusScanUpdate.PathDisplayStarted -> if (state.isScanning)
                state.copy(isPathMode = true, currentLabel = "", currentIcon = "@protection") else state
            is VirusScanUpdate.PathDisplayed -> if (state.isScanning && state.isPathMode)
                state.copy(currentLabel = update.path) else state
            VirusScanUpdate.ScanFinished -> state.copy(isScanning = false, scanCompleted = true, progressFraction = 1f)
        }

    private fun VirusScanUiState.withThreats(next: List<VirusThreatModel>) = copy(
        threats = next,
        appThreatCount = next.count { !it.isFile },
        fileThreatCount = next.count { it.isFile },
    )
}
