package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui

import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanIntent
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanMode
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanViewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.VirusScanningView

@Composable
fun QuickScanVirusRoute(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    VirusScanRoute(
        mode = VirusScanMode.Quick,
        navigator = navigator,
        viewModel = viewModel,
    )
}

@Composable
fun DeepScanVirusRoute(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    VirusScanRoute(
        mode = VirusScanMode.Deep,
        navigator = navigator,
        viewModel = viewModel,
    )
}

@Composable
private fun VirusScanRoute(
    mode: VirusScanMode,
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(mode) {
        viewModel.dispatch(VirusScanIntent.StartScan(mode))
    }

    DisposableEffect(mode) {
        onDispose { if (viewModel.state.value.isScanning) viewModel.dispatch(VirusScanIntent.CancelScan) }
    }

    VirusScanningView(
        mode = mode,
        uiState = uiState,
    )
}
