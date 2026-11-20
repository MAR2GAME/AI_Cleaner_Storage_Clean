package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui


import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerEvent

import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerPage
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerAction
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerViewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate
import com.aicleanerstorage.spaceclean.base.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.NotificationCleanerScreen

@Composable
fun NotificationCleanerRoute(
    navigator: AppNavigator,
    viewModel: NotificationCleanerViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val permissionGate = rememberPermissionGate()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.dispatch(NotificationCleanerAction.RefreshRequested)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun exitBack() {
        if (!exiting) {
            exiting = true
            routeScope.launch {
                ensureActive()
                navigator.back()
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.event.collect { effect ->
            when (effect) {
                NotificationCleanerEvent.Exit -> exitBack()
                is NotificationCleanerEvent.Navigate -> if (!exiting) navigator.navigate(effect.destination)
            }
        }
    }





    NotificationCleanerScreen(
        state = state,
        onAction = actions@{ action ->
            if (exiting) return@actions
            when (action) {
                NotificationCleanerAction.BackClicked -> viewModel.dispatch(action)
                NotificationCleanerAction.StopConfirmed -> {
                    viewModel.dispatch(action)
                }
                NotificationCleanerAction.BlockingEnableRequested ->
                    permissionGate.allow(AppPermission.NotificationListener) { viewModel.dispatch(action) }
                else -> viewModel.dispatch(action)
            }
        },
        onNavigate = { if (!exiting) viewModel.dispatch(NotificationCleanerAction.NavigationRequested(it.toAppDestination())) },
    )
}
