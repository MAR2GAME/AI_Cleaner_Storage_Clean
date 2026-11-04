package com.aicleanerstorage.spaceclean.feature.toolbox.networkscan

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.common.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.*

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.*

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
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionGateway
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.common.permission.PermissionPromptMode
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.ui.NetworkScanScreen

@Composable
fun NetworkScanRoute(
    navigator: AppNavigator,
    viewModel: NetworkScanViewModel,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    val routeScope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionGateway = LocalPermissionGateway.current

    fun handleBack() {
        if (exiting) return
        if (uiState.phase == NetworkScanPhase.Scanning) viewModel.cancelScan()
        run {
            if (!exiting) {
                exiting = true
                permissionJob?.cancel()
                routeScope.launch {
                    featureFlow.exit(FeatureKey.NETWORK_SCAN, FeatureExitReason.Return)
                    ensureActive()
                    navigator.back()
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.refreshNetworkStateUntilWifiConnected()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(onBack = ::handleBack)

    NetworkScanScreen(
        uiState = uiState,
        onBack = ::handleBack,
        onRefreshWifi = viewModel::refreshNetworkStateUntilWifiConnected,
        onSwitchWifi = {
            val openedSettings =
                openWifiSettings(
                    context = context,
                    onLaunchingSettings = externalActivities.markLaunch,
                    onSettingsLaunchFailed = externalActivities.cancelLaunch,
                )
            if (!openedSettings) externalActivities.cancelLaunch()
            viewModel.refreshNetworkStateUntilWifiConnected()
        },
        onScan = {
            if (!exiting && permissionJob?.isActive != true) {
                permissionJob = routeScope.launch {
                    when (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.NetworkScanStart), mode = PermissionPromptMode.Direct)) {
                        PermissionResult.Granted -> { viewModel.startScan() }
                        PermissionResult.Denied -> { Unit }
                        PermissionResult.Busy -> Unit
                    }
                }
            }
        },
        onOpenDevices = { navigator.open(AppDestination.NetworkScanDevices) },
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
