package com.aicleanerstorage.spaceclean.app.view.antivirus.ui


import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanAction
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanEvent
import com.aicleanerstorage.spaceclean.domain.model.VirusScanMode
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanViewModel


import androidx.compose.runtime.*
import kotlinx.coroutines.*

import android.content.ActivityNotFoundException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate

@Composable
fun VirusEffectHandler(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    val currentNavigator by rememberUpdatedState(navigator)
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val permissionGate = rememberPermissionGate()
    LaunchedEffect(viewModel) {
        viewModel.event.collect { effect ->
            when (effect) {
                VirusScanEvent.NavigateBack -> currentNavigator.back()
                is VirusScanEvent.Navigate -> currentNavigator.resetToRoot(effect.destination)
                VirusScanEvent.OpenDeveloperSettings -> openDeveloperSettings(context)
                is VirusScanEvent.OpenAppSettings -> openAppSettings(context, effect.packageName)
                is VirusScanEvent.DeleteFile -> {
                    if (java.io.File(effect.path).safeDelete(context)) {
                        viewModel.dispatch(VirusScanAction.FileThreatRemovalRequested(effect.path))
                    } else {
                        android.widget.Toast.makeText(context, com.aicleanerstorage.spaceclean.R.string.deletion_failed,
                            android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                is VirusScanEvent.NavigateToResult -> currentNavigator.replace(
                    if (effect.hasThreats) AppDestination.VirusResult else AppDestination.NoVirusResult,
                )
                VirusScanEvent.Exit ->
                    {
                        if (!exiting) {
                            exiting = true
                            routeScope.launch {
                                ensureActive()
                                currentNavigator.back()
                            }
                        }
                    }
                VirusScanEvent.OpenInstalledAppsSettings -> {
                    try {
                        context.startActivity(android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:${context.packageName}")))
                    } catch (_: ActivityNotFoundException) {
                        viewModel.dispatch(VirusScanAction.SettingsLaunchFailed)
                    } catch (_: Exception) {
                        viewModel.dispatch(VirusScanAction.SettingsLaunchFailed)
                    }
                }
                is VirusScanEvent.RequestDeepScanPermission ->
                    permissionGate.request(AppPermission.StorageFiles) { granted ->
                        viewModel.dispatch(VirusScanAction.DeepScanPermissionResultReceived(granted))
                    }
                is VirusScanEvent.NavigateToScan -> {
                    currentNavigator.navigate(
                        when (effect.mode) {
                            VirusScanMode.Quick -> AppDestination.VirusQuickScan
                            VirusScanMode.Deep -> AppDestination.VirusDeepScan
                        },
                    )
                }
            }
        }
    }
}
