package com.aicleanerstorage.spaceclean.feature.antivirus

import android.content.ActivityNotFoundException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.common.permission.appSettingsIntent
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.feature.antivirus.ui.AntiVirusScreen

@Composable
fun AntiVirusRoute(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    val state by viewModel.homeState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionCoordinator = LocalPermissionCoordinator.current

    LaunchedEffect(viewModel, navigator, featureFlow, externalActivities) {
        viewModel.onHomeAction(VirusHomeAction.Entered)
        viewModel.homeEffects.collect { effect ->
            when (effect) {
                VirusHomeEffect.Exit ->
                    featureFlow.exit(FeatureKey.ANTI_VIRUS, FeatureExitReason.Return) { navigator.back() }
                VirusHomeEffect.OpenInstalledAppsSettings -> {
                    externalActivities.markLaunch()
                    try {
                        context.startActivity(appSettingsIntent(context))
                    } catch (_: ActivityNotFoundException) {
                        externalActivities.cancelLaunch()
                        viewModel.onHomeAction(VirusHomeAction.SettingsLaunchFailed)
                    } catch (_: Exception) {
                        externalActivities.cancelLaunch()
                        viewModel.onHomeAction(VirusHomeAction.SettingsLaunchFailed)
                    }
                }
                is VirusHomeEffect.RequestDeepScanPermission -> {
                    permissionCoordinator.ensure(
                        action = ProtectedAction.VirusDeepScanStart,
                        onGranted = {
                            viewModel.onHomeAction(VirusHomeAction.DeepScanPermissionResult(granted = true))
                        },
                        onDenied = {
                            viewModel.onHomeAction(VirusHomeAction.DeepScanPermissionResult(granted = false))
                        },
                    )
                }
                is VirusHomeEffect.NavigateToScan -> {
                    navigator.open(
                        when (effect.mode) {
                            VirusScanMode.Quick -> AppDestination.VirusQuickScan
                            VirusScanMode.Deep -> AppDestination.VirusDeepScan
                        },
                    )
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onHomeAction(VirusHomeAction.Resumed)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AntiVirusScreen(
        state = state,
        onAction = viewModel::onHomeAction,
    )
}
