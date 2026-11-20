package com.aicleanerstorage.spaceclean.app.view.antivirus.ui

import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanAction
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanViewModel


import androidx.compose.runtime.*
import kotlinx.coroutines.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.AntiVirusScreen

@Composable
fun AntiVirusRoute(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val flowState by viewModel.state.collectAsStateWithLifecycle()
    val state = flowState.home
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel) { viewModel.dispatch(VirusScanAction.Entered) }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.dispatch(VirusScanAction.Resumed)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AntiVirusScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}
