package com.aicleanerstorage.spaceclean.feature.antivirus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.antivirus.VirusResultAction
import com.aicleanerstorage.spaceclean.feature.antivirus.VirusScanUiState

@Composable
fun ScanVirusResultScreen(
    state: VirusScanUiState,
    onAction: (VirusResultAction) -> Unit,
) {
    var fileToDelete by remember { mutableStateOf<String?>(null) }

    fileToDelete?.let { path ->
        DeleteVirusFileDialog(
            onConfirm = {
                fileToDelete = null
                onAction(VirusResultAction.DeleteFileThreat(path))
            },
            onDismiss = { fileToDelete = null },
        )
    }

    VirusThreatResultView(
        uiState = state,
        onSolveAdbRisk = { onAction(VirusResultAction.SolveAdbRisk) },
        onSolveThreat = { threat ->
            if (threat.isFile) {
                threat.apkPath?.let { fileToDelete = it }
            } else {
                threat.packageName?.let { onAction(VirusResultAction.SolveAppThreat(it)) }
            }
        },
        onBack = { onAction(VirusResultAction.Back) },
    )
    BackHandler { onAction(VirusResultAction.Back) }
}

@Composable
fun NoVirusResultScreen(
    onAction: (VirusResultAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
) {
    BackHandler { onAction(VirusResultAction.Back) }
    NoVirusResultView(
        onNavigateTool = onNavigate,
        onBack = { onAction(VirusResultAction.Back) },
    )
}
