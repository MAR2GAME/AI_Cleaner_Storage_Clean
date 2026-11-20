package com.aicleanerstorage.spaceclean.app.view.antivirus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanAction
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanUiState
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part.DeleteVirusFileDialog
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part.VirusThreatResultContent

@Composable
fun ScanVirusResultScreen(
    state: VirusScanUiState,
    onAction: (VirusScanAction) -> Unit,
) {
    var fileToDelete by remember { mutableStateOf<String?>(null) }

    fileToDelete?.let { path ->
        DeleteVirusFileDialog(
            onConfirm = {
                fileToDelete = null
                onAction(VirusScanAction.FileThreatDeleteClicked(path))
            },
            onDismiss = { fileToDelete = null },
        )
    }

    VirusThreatResultContent(
        uiState = state,
        onSolveAdbRisk = { onAction(VirusScanAction.AdbRiskResolutionClicked) },
        onSolveThreat = { threat ->
            if (threat.isFile) {
                threat.apkPath?.let { fileToDelete = it }
            } else {
                threat.packageName?.let { onAction(VirusScanAction.AppThreatResolutionClicked(it)) }
            }
        },
        onBack = { onAction(VirusScanAction.ResultBackClicked) },
    )
    BackHandler { onAction(VirusScanAction.ResultBackClicked) }
}
