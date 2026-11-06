package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui

import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanIntent

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanUiState

@Composable
fun ScanVirusResultScreen(
    state: VirusScanUiState,
    onAction: (VirusScanIntent) -> Unit,
) {
    var fileToDelete by remember { mutableStateOf<String?>(null) }

    fileToDelete?.let { path ->
        DeleteVirusFileDialog(
            onConfirm = {
                fileToDelete = null
                onAction(VirusScanIntent.DeleteFileThreat(path))
            },
            onDismiss = { fileToDelete = null },
        )
    }

    VirusThreatResultView(
        uiState = state,
        onSolveAdbRisk = { onAction(VirusScanIntent.SolveAdbRisk) },
        onSolveThreat = { threat ->
            if (threat.isFile) {
                threat.apkPath?.let { fileToDelete = it }
            } else {
                threat.packageName?.let { onAction(VirusScanIntent.SolveAppThreat(it)) }
            }
        },
        onBack = { onAction(VirusScanIntent.ResultBack) },
    )
    BackHandler { onAction(VirusScanIntent.ResultBack) }
}

@Composable
fun NoVirusResultScreen(
    onAction: (VirusScanIntent) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
) {
    BackHandler { onAction(VirusScanIntent.ResultBack) }
    NoVirusResultView(
        onNavigateTool = onNavigate,
        onBack = { onAction(VirusScanIntent.ResultBack) },
    )
}
