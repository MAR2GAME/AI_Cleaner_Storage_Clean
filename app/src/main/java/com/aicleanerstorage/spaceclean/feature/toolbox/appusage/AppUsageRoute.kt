package com.aicleanerstorage.spaceclean.feature.toolbox.appusage

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.common.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult

import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.*


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.permission.PermissionType
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionGateway
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageEffect
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.ui.AppUsageScreen

@Composable
fun AppUsageRoute(
    navigator: AppNavigator,
    viewModel: AppUsageViewModel,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    val routeScope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val permissionGateway = LocalPermissionGateway.current
    LaunchedEffect(viewModel, context, externalActivities) {
        viewModel.effects.collect { effect ->
            when (effect) {
                    is AppUsageEffect.OpenAppInfo -> {
                        val intent = Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${effect.packageName}"),
                        )
                        try {
                            externalActivities.markLaunch()
                            context.startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            externalActivities.cancelLaunch()
                        } catch (_: Exception) {
                            externalActivities.cancelLaunch()
                        }
                    }
                    is AppUsageEffect.Exit -> {
                        val reason = when (effect.reason) {
                            AppUsageExitReason.Return -> FeatureExitReason.Return
                            AppUsageExitReason.PermissionRejected -> FeatureExitReason.PermissionRejected
                        }
                    run {
                        if (!exiting) {
                            exiting = true
                            permissionJob?.cancel()
                            routeScope.launch {
                                featureFlow.exit(FeatureKey.APP_USAGE, reason)
                                ensureActive()
                                navigator.back()
                            }
                        }
                    }
                    }
                }
            }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.onAction(AppUsageAction.Resumed)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.permissionRequestPending) {
        if (uiState.permissionRequestPending) {
            viewModel.onAction(AppUsageAction.PermissionRequestConsumed)
            if (!exiting && permissionJob?.isActive != true) {
                permissionJob = routeScope.launch {
                    when (permissionGateway.ensure(PermissionTarget.Permission(PermissionType.UsageAccess))) {
                        PermissionResult.Granted -> { viewModel.onAction(AppUsageAction.PermissionGranted) }
                        PermissionResult.Denied -> { viewModel.onAction(AppUsageAction.PermissionRejected) }
                        PermissionResult.Busy -> Unit
                    }
                }
            }
        }
    }

    BackHandler { viewModel.onAction(AppUsageAction.Back) }

    AppUsageScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.onAction(it) },
    )
}
