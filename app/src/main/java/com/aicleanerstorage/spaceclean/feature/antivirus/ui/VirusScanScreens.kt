package com.aicleanerstorage.spaceclean.feature.antivirus.ui

import com.aicleanerstorage.spaceclean.feature.antivirus.*

import com.aicleanerstorage.spaceclean.feature.antivirus.VirusScanViewModel

import com.aicleanerstorage.spaceclean.feature.antivirus.VirusScanMode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.feature.antivirus.ui.VirusScanningView

@Composable
fun QuickScanVirusScreen(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    VirusScanContent(
        mode = VirusScanMode.Quick,
        navigator = navigator,
        viewModel = viewModel,
    )
}

@Composable
fun DeepScanVirusScreen(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    VirusScanContent(
        mode = VirusScanMode.Deep,
        navigator = navigator,
        viewModel = viewModel,
    )
}

@Composable
private fun VirusScanContent(
    mode: VirusScanMode,
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    val router = navigator
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var scanStarted by remember(mode) { mutableStateOf(false) }

    LaunchedEffect(mode) {
        scanStarted = false
        viewModel.startScan(mode)
        scanStarted = true
    }

    LaunchedEffect(scanStarted, uiState.scanCompleted, uiState.effectiveThreatCount) {
        if (scanStarted && uiState.scanCompleted) {
            if (uiState.effectiveThreatCount > 0) {
                router.replace(AppDestination.VirusResult)
            } else {
                router.replace(AppDestination.NoVirusResult)
            }
        }
    }

    DisposableEffect(mode) {
        onDispose { viewModel.cancelScan() }
    }

    VirusScanningView(
        mode = mode,
        uiState = uiState,
    )
}
