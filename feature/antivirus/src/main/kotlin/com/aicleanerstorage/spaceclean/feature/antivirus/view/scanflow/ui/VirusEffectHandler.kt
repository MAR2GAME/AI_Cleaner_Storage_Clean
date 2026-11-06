package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui

import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult

import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanIntent
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanEffect
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanMode
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanViewModel


import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionResult

import android.content.ActivityNotFoundException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.core.ui.permission.LocalPermissionGateway

@Composable
fun VirusEffectHandler(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    val currentNavigator by rememberUpdatedState(navigator)
    val routeScope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val permissionGateway = LocalPermissionGateway.current
    LaunchedEffect(viewModel, featureFlow, externalActivities) {
        viewModel.effects.collect { effect ->
            when (effect) {
                VirusScanEffect.Back -> currentNavigator.back()
                is VirusScanEffect.Navigate -> currentNavigator.resetTo(effect.destination)
                VirusScanEffect.OpenDeveloperSettings -> openDeveloperSettings(context, externalActivities)
                is VirusScanEffect.OpenAppSettings -> openAppSettings(context, effect.packageName, externalActivities)
                is VirusScanEffect.DeleteFile -> {
                    if (java.io.File(effect.path).safeDelete(context)) {
                        viewModel.dispatch(VirusScanIntent.RemoveThreatByFilePath(effect.path))
                    } else {
                        android.widget.Toast.makeText(context, com.aicleanerstorage.spaceclean.core.ui.R.string.deletion_failed,
                            android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                is VirusScanEffect.NavigateToResult -> currentNavigator.replaceCurrent(
                    if (effect.hasThreats) AppDestination.VirusResult else AppDestination.NoVirusResult,
                )
                VirusScanEffect.Exit ->
                    {
                        if (!exiting) {
                            exiting = true
                            permissionJob?.cancel()
                            routeScope.launch {
                                featureFlow.exit(FeatureKey.ANTI_VIRUS, FeatureExitReason.Return)
                                ensureActive()
                                currentNavigator.back()
                            }
                        }
                    }
                VirusScanEffect.OpenInstalledAppsSettings -> {
                    externalActivities.markLaunch()
                    try {
                        context.startActivity(android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:${context.packageName}")))
                    } catch (_: ActivityNotFoundException) {
                        externalActivities.cancelLaunch()
                        viewModel.dispatch(VirusScanIntent.SettingsLaunchFailed)
                    } catch (_: Exception) {
                        externalActivities.cancelLaunch()
                        viewModel.dispatch(VirusScanIntent.SettingsLaunchFailed)
                    }
                }
                is VirusScanEffect.RequestDeepScanPermission -> {
                    if (!exiting && permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            when (ActionPermissionGate(permissionGateway).checkAction(ProtectedAction.VirusDeepScanStart)) {
                                GateResult.Allow -> { viewModel.dispatch(VirusScanIntent.DeepScanPermissionResult(granted = true)) }
                                is GateResult.Deny -> { viewModel.dispatch(VirusScanIntent.DeepScanPermissionResult(granted = false)) }
                                GateResult.Busy -> Unit
                            }
                        }
                    }
                }
                is VirusScanEffect.NavigateToScan -> {
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
