package com.aicleanerstorage.spaceclean.app.view.networkscan.ui


import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanEvent

import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanPhase
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanAction


import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanRoute


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
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanScreen

@Composable
fun NetworkScanRoute(
    navigator: AppNavigator,
    viewModel: NetworkScanViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val permissionGate = rememberPermissionGate()

    fun handleBack() {
        if (exiting) return
        if (!exiting) {
            exiting = true
            routeScope.launch {
                ensureActive()
                navigator.back()
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(NetworkScanAction.WifiRefreshRequested)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel, navigator) {
        viewModel.event.collect { effect ->
            when (effect) {
                NetworkScanEvent.Exit -> handleBack()
                NetworkScanEvent.OpenDevices -> if (!exiting) navigator.navigate(AppDestination.NetworkScanDevices)
                NetworkScanEvent.OpenWifiSettings -> {

            val openedSettings =
                openWifiSettings(context = context)
            viewModel.dispatch(NetworkScanAction.WifiRefreshRequested)
        
                }
            }
        }
    }
    BackHandler { viewModel.dispatch(NetworkScanAction.BackClicked) }

    NetworkScanScreen(
        uiState = uiState,
        onBack = { viewModel.dispatch(NetworkScanAction.BackClicked) },
        onRefreshWifi = { viewModel.dispatch(NetworkScanAction.WifiRefreshRequested) },
        onSwitchWifi = { viewModel.dispatch(NetworkScanAction.SwitchWifiClicked) },
        onScan = {
            if (!exiting) {
                permissionGate.allow(AppPermission.Location) { viewModel.dispatch(NetworkScanAction.ScanStartRequested) }
            }
        },
        onOpenDevices = { viewModel.dispatch(NetworkScanAction.DevicesClicked) },
    )
}

private fun openWifiSettings(
    context: Context,
): Boolean {
    val intents =
        buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) add(Intent(Settings.Panel.ACTION_WIFI))
            add(Intent(Settings.ACTION_WIFI_SETTINGS))
            add(Intent(Settings.ACTION_SETTINGS))
        }
    intents.forEach { intent ->
        try {
            context.startActivity(intent)
            return true
        } catch (_: ActivityNotFoundException) {
        } catch (_: Exception) {
        }
    }
    return false
}

