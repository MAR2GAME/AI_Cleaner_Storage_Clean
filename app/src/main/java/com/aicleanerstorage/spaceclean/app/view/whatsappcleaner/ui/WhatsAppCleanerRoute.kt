package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui


import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerEvent

import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerAction
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerViewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate
import com.aicleanerstorage.spaceclean.base.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.WhatsAppCleanerScreen

@Composable
fun WhatsAppCleanerRoute(
    navigator: AppNavigator,
    viewModel: WhatsAppCleanerViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val permissionGate = rememberPermissionGate()

    fun exitOnce(navigate: () -> Unit) {
        if (exiting) return
        exiting = true
        routeScope.launch {
            ensureActive()
            navigate()
        }
    }

    LaunchedEffect(viewModel) {
        permissionGate.allow(AppPermission.StorageFiles) {
            viewModel.dispatch(WhatsAppCleanerAction.ScanStartRequested)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.event.collect { effect ->
            when (effect) {
                is WhatsAppCleanerEvent.Exit ->
                    exitOnce {
                        if (effect.home) { viewModel.dispatch(WhatsAppCleanerAction.ResultDismissed); navigator.resetToRoot(AppDestination.Main()) }
                        else navigator.back()
                    }
                is WhatsAppCleanerEvent.Navigate -> if (!exiting) navigator.navigate(effect.destination)
            }
        }
    }

    WhatsAppCleanerScreen(
        state = state,
        onAction = actions@{ action ->
            if (exiting) return@actions
            when (action) {
                WhatsAppCleanerAction.CleanClicked -> {
                    if (!exiting) {
                        permissionGate.allow(AppPermission.StorageFiles) { viewModel.dispatch(action) }
                    }
                }
                else -> viewModel.dispatch(action)
            }
        },
        onNavigate = { if (!exiting) viewModel.dispatch(WhatsAppCleanerAction.NavigationRequested(it.toAppDestination())) },
    )
}
