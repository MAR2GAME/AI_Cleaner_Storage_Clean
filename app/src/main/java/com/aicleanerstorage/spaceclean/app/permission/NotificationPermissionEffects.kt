package com.aicleanerstorage.spaceclean.app.permission

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionResult

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionPromptMode

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionTarget

import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.awaitCancellation
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
    val granted by rememberUpdatedState(onPermissionGranted)
    val surface = when {
        currentRoute == null || currentRoute == AppDestination.Splash.route -> NotificationPermissionSurface.Splash
        currentRoute in AppDestination.homeRoutes -> NotificationPermissionSurface.Home
        else -> NotificationPermissionSurface.Other
    }
    LaunchedEffect(surface) { viewModel.onSurfaceChanged(surface, context.postNotificationRationale()) }
    LaunchedEffect(viewModel, lifecycleOwner, surface, context) {
        if (surface == NotificationPermissionSurface.Home) {
            lifecycleOwner.lifecycle.observeNotificationHomePrompt {
                viewModel.onAppResumed(context.postNotificationRationale())
            }
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
}

internal suspend fun Lifecycle.observeNotificationHomePrompt(onReady: () -> Unit) {
    repeatOnLifecycle(Lifecycle.State.RESUMED) {
        delay(HOME_NOTIFICATION_PERMISSION_PROMPT_DELAY_MILLIS)
        onReady()
        awaitCancellation()
    }
}
