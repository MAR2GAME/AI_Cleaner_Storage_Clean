package com.aicleanerstorage.spaceclean.app.view.appusage.ui

import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageEvent
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageExitReason
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageAction
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageViewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.app.view.appusage.ui.AppUsageRoute


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
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate
import com.aicleanerstorage.spaceclean.app.view.appusage.ui.AppUsageScreen

@Composable
fun AppUsageRoute(
    navigator: AppNavigator,
    viewModel: AppUsageViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val permissionGate = rememberPermissionGate()
    LaunchedEffect(uiState.permissionRequestPending) {
        if (uiState.permissionRequestPending) {
            viewModel.dispatch(AppUsageAction.PermissionRequestConsumed)
            permissionGate.request(AppPermission.UsageAccess) { granted ->
                viewModel.dispatch(if (granted) AppUsageAction.PermissionGranted else AppUsageAction.PermissionRejected)
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    LaunchedEffect(viewModel, context) {
        viewModel.event.collect { effect ->
            when (effect) {
                    is AppUsageEvent.OpenAppInfo -> {
                        val intent = Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${effect.packageName}"),
                        )
                        try {
                            context.startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                        } catch (_: Exception) {
                        }
                    }
                    is AppUsageEvent.Exit -> {
                    if (!exiting) {
                        exiting = true
                        routeScope.launch {
                            ensureActive()
                            navigator.back()
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
                    viewModel.dispatch(AppUsageAction.Resumed)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler { viewModel.dispatch(AppUsageAction.BackClicked) }

    AppUsageScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}

