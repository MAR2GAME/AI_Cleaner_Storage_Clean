package com.aicleanerstorage.spaceclean.app.view.antivirus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusHomeUiState
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanAction
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part.AntiVirusHomeContent
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part.VirusInstalledAppsAccessDialog
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part.VirusScanNoticeDialog
import com.aicleanerstorage.spaceclean.domain.security.model.VirusScanMode

@Composable
fun AntiVirusScreen(
    state: VirusHomeUiState,
    onAction: (VirusScanAction) -> Unit,
) {
    AntiVirusHomeContent(
        onDeepScan = { onAction(VirusScanAction.ScanRequested(VirusScanMode.Deep)) },
        onQuickScan = { onAction(VirusScanAction.ScanRequested(VirusScanMode.Quick)) },
        onBack = { onAction(VirusScanAction.BackClicked) },
        enabled = !state.scanRequestPending,
    )

    BackHandler { onAction(VirusScanAction.BackClicked) }

    if (state.showScanNotice) {
        VirusScanNoticeDialog(
            onAccept = { onAction(VirusScanAction.ScanNoticeAccepted) },
            onDecline = { onAction(VirusScanAction.ScanNoticeDeclined) },
        )
    }

    if (state.showInstalledAppsPermissionDialog) {
        VirusInstalledAppsAccessDialog(
            onOpenSettings = { onAction(VirusScanAction.InstalledAppsSettingsRequested) },
            onDismiss = { onAction(VirusScanAction.InstalledAppsDialogDismissed) },
        )
    }
}
