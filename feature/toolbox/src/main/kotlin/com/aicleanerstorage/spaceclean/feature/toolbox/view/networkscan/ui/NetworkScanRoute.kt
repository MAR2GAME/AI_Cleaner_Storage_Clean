package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui


import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanEffect

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanIntent


import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui.NetworkScanRoute


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui.NetworkScanScreen

@Composable
fun NetworkScanRoute(
    navigator: AppNavigator,
    viewModel: NetworkScanViewModel,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    fun handleBack() {
        if (exiting) return
        if (!exiting) {
            exiting = true
            routeScope.launch {
                featureFlow.exit(FeatureKey.NETWORK_SCAN, FeatureExitReason.Return)
                ensureActive()
                navigator.back()
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(NetworkScanIntent.RefreshNetworkStateUntilWifiConnected)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                NetworkScanEffect.Exit -> handleBack()
                NetworkScanEffect.OpenDevices -> if (!exiting) navigator.navigate(AppDestination.NetworkScanDevices)
                NetworkScanEffect.OpenWifiSettings -> {

            val openedSettings =
                openWifiSettings(
                    context = context,
                    onLaunchingSettings = externalActivities.markLaunch,
                    onSettingsLaunchFailed = externalActivities.cancelLaunch,
                )
            if (!openedSettings) externalActivities.cancelLaunch()
            viewModel.dispatch(NetworkScanIntent.RefreshNetworkStateUntilWifiConnected)
        
                }
            }
        }
    }
    BackHandler { viewModel.dispatch(NetworkScanIntent.Back) }

    NetworkScanScreen(
        uiState = uiState,
        onBack = { viewModel.dispatch(NetworkScanIntent.Back) },
        onRefreshWifi = { viewModel.dispatch(NetworkScanIntent.RefreshNetworkStateUntilWifiConnected) },
        onSwitchWifi = { viewModel.dispatch(NetworkScanIntent.SwitchWifi) },
        onScan = {
            if (!exiting) viewModel.dispatch(NetworkScanIntent.StartScan)
        },
        onOpenDevices = { viewModel.dispatch(NetworkScanIntent.OpenDevices) },
    )
}

private fun openWifiSettings(
    context: Context,
    onLaunchingSettings: () -> Unit,
    onSettingsLaunchFailed: () -> Unit,
): Boolean {
    val intents =
        buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) add(Intent(Settings.Panel.ACTION_WIFI))
            add(Intent(Settings.ACTION_WIFI_SETTINGS))
            add(Intent(Settings.ACTION_SETTINGS))
        }
    intents.forEach { intent ->
        try {
            onLaunchingSettings()
            context.startActivity(intent)
            return true
        } catch (_: ActivityNotFoundException) {
            onSettingsLaunchFailed()
        } catch (_: Exception) {
            onSettingsLaunchFailed()
        }
    }
    return false
}

