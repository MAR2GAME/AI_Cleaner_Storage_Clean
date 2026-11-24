package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.common.permission.rememberPermissionPrompt
import com.aicleanerstorage.spaceclean.app.view.common.permission.whatsAppCopy
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerAction
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerEvent
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerPhase
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

@Composable
fun WhatsAppCleanerRoute(
    navigator: AppNavigator,
    viewModel: WhatsAppCleanerViewModel,
    onCompleted: () -> Unit = {},
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.phase) {
        if (state.phase == WhatsAppCleanerPhase.CompleteAnimation) onCompleted()
    }
    val permissionPrompt = rememberPermissionPrompt(copy = { permission -> permission.whatsAppCopy() })

    fun gated(action: WhatsAppCleanerAction) {
        if (exiting) return
        permissionPrompt.request(AppPermission.StorageFiles) { result ->
            if (result == PermissionResult.Granted) {
                viewModel.dispatch(action)
            } else {
                viewModel.dispatch(WhatsAppCleanerAction.PermissionDenied)
            }
        }
    }

    fun exitOnce(navigate: () -> Unit) {
        if (exiting) return
        exiting = true
        routeScope.launch {
            ensureActive()
            navigate()
        }
    }

    LaunchedEffect(viewModel) {
        gated(WhatsAppCleanerAction.ScanStartRequested)
    }

    CollectEffects(viewModel) { effect ->
        when (effect) {
            is WhatsAppCleanerEvent.Exit ->
                exitOnce {
                    if (effect.home) {
                        viewModel.dispatch(WhatsAppCleanerAction.ResultDismissed)
                        navigator.resetToRoot(AppDestination.Main())
                    } else {
                        navigator.back()
                    }
                }

            is WhatsAppCleanerEvent.Navigate -> if (!exiting) navigator.navigate(effect.destination)
        }
    }

    WhatsAppCleanerScreen(
        state = state,
        onAction = { action ->
            if (exiting) return@WhatsAppCleanerScreen
            when (action) {
                WhatsAppCleanerAction.CleanClicked -> gated(action)
                is WhatsAppCleanerAction.NavigationRequested -> if (!exiting) viewModel.dispatch(action)
                else -> viewModel.dispatch(action)
            }
        },
    )
}
