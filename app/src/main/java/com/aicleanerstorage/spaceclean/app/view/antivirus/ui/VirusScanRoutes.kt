package com.aicleanerstorage.spaceclean.app.view.antivirus.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanAction
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanViewModel
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part.VirusScanLoading
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.data.security.model.VirusScanMode

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
        viewModel.dispatch(VirusScanAction.ScanStartRequested(mode))
    }

    DisposableEffect(mode) {
        onDispose { if (viewModel.state.value.isScanning) viewModel.dispatch(VirusScanAction.ScanCancellationRequested) }
    }

    VirusScanLoading(
        mode = mode,
        uiState = uiState,
    )
}
