package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui

import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanIntent
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanViewModel


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
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.AntiVirusScreen

@Composable
fun AntiVirusRoute(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val flowState by viewModel.state.collectAsStateWithLifecycle()
    val state = flowState.home
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel) { viewModel.dispatch(VirusScanIntent.Entered) }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.dispatch(VirusScanIntent.Resumed)
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
