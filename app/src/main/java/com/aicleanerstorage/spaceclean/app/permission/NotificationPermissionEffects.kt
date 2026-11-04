package com.aicleanerstorage.spaceclean.app.permission

import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.permission.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

@Composable
internal fun NotificationPermissionEffects(
    viewModel: NotificationPermissionViewModel,
    currentRoute: String?,
    permission: PermissionInterceptor,
    onPermissionGranted: () -> Unit,
) {
    if (Build.VERSION.SDK_INT < 33) return
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val granted by rememberUpdatedState(onPermissionGranted)
    val surface = when {
        currentRoute == null || currentRoute == AppDestination.Splash.route -> NotificationPermissionSurface.Splash
        currentRoute in AppDestination.homeRoutes -> NotificationPermissionSurface.Home
        else -> NotificationPermissionSurface.Other
    }
    LaunchedEffect(surface) { viewModel.onSurfaceChanged(surface, context.postNotificationRationale()) }
    LaunchedEffect(surface) {
        if (surface == NotificationPermissionSurface.Home) {
            delay(HOME_NOTIFICATION_PERMISSION_PROMPT_DELAY_MILLIS)
            viewModel.onHomePromptDelayElapsed()
        }
    }
    LaunchedEffect(viewModel, permission, surface) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is NotificationPermissionCommand.RequestSystemPermission -> {
                    while (true) {
                        val phase = viewModel.uiState.value
                        val expected = if (effect.source == NotificationPermissionRequestSource.Splash)
                            NotificationPermissionSurface.Splash else NotificationPermissionSurface.Home
                        if (phase.surface != expected || phase.phase !is NotificationPermissionPhase.RequestingSystem) break
                        val result = permission.ensure(PermissionTarget.Permission(PermissionType.PostNotifications), PermissionPromptMode.Direct)
                        if (result == PermissionResult.Busy) {
                            permission.request.first { it == null }
                            continue
                        }
                        if (viewModel.uiState.value.phase is NotificationPermissionPhase.RequestingSystem) {
                            if (result == PermissionResult.Granted) viewModel.onSystemPermissionResult(true, context.postNotificationRationale())
                            else viewModel.onSystemRequestUnavailable()
                        }
                        break
                    }
                }
                NotificationPermissionCommand.OpenAppSettings -> {
                    while (viewModel.uiState.value.surface == NotificationPermissionSurface.Home &&
                        viewModel.uiState.value.phase == NotificationPermissionPhase.LaunchingSettings) {
                        val result = permission.openSettings(PermissionType.PostNotifications)
                        if (result == PermissionResult.Busy) { permission.request.first { it == null }; continue }
                        viewModel.onSettingsFinished(context.postNotificationRationale())
                        break
                    }
                }
                NotificationPermissionCommand.NotifyPermissionGranted -> granted()
            }
        }
    }
    DisposableEffect(lifecycleOwner, viewModel, context) {
        var refresh: Job? = null
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && viewModel.uiState.value.surface == NotificationPermissionSurface.Home) {
                refresh?.cancel()
                refresh = scope.launch {
                    delay(HOME_NOTIFICATION_PERMISSION_PROMPT_DELAY_MILLIS)
                    if (viewModel.uiState.value.surface == NotificationPermissionSurface.Home) {
                        viewModel.onAppResumed(context.postNotificationRationale())
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { refresh?.cancel(); lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
