package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanIntent
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusHomeUiState
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanMode

@Composable
fun AntiVirusScreen(
    state: VirusHomeUiState,
    onAction: (VirusScanIntent) -> Unit,
) {
    AntiVirusHomeView(
        onDeepScan = { onAction(VirusScanIntent.ScanRequested(VirusScanMode.Deep)) },
        onQuickScan = { onAction(VirusScanIntent.ScanRequested(VirusScanMode.Quick)) },
        onBack = { onAction(VirusScanIntent.Back) },
        enabled = !state.scanRequestPending,
    )

    BackHandler { onAction(VirusScanIntent.Back) }

    if (state.showScanNotice) {
        VirusInstalledAppsAccessDialog(
            title = stringResource(R.string.virus_app_list_permission_title),
            message = stringResource(R.string.virus_app_list_permission_message),
            primaryText = stringResource(R.string.virus_app_list_permission_agree),
            onPrimaryAction = { onAction(VirusScanIntent.ScanNoticeAccepted) },
            secondaryText = stringResource(R.string.virus_app_list_permission_not_now),
            onSecondaryAction = { onAction(VirusScanIntent.ScanNoticeDeclined) },
            onDismissRequest = { onAction(VirusScanIntent.ScanNoticeDeclined) },
            showHeroImage = true,
        )
    }

    if (state.showInstalledAppsPermissionDialog) {
        VirusInstalledAppsAccessDialog(
            title = stringResource(R.string.permission_title_required),
            message = stringResource(R.string.permission_installed_apps_desc),
            primaryText = stringResource(R.string.manage_permission),
            onPrimaryAction = { onAction(VirusScanIntent.InstalledAppsSettingsRequested) },
            secondaryText = stringResource(R.string.not_now),
            onSecondaryAction = { onAction(VirusScanIntent.InstalledAppsDialogDismissed) },
            onDismissRequest = { onAction(VirusScanIntent.InstalledAppsDialogDismissed) },
        )
    }
}
