package com.aicleanerstorage.spaceclean.app.view.permissions.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdGateway
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsAction
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsEvent
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsViewModel
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionGate
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate

@Composable
fun ManagePermissionsRoute(
    navigator: AppNavigator,
    viewModel: ManagePermissionsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    fun refreshStatuses() {
        val statuses =
            AppPermission.entries.associateWith { permission ->
                permission.isGranted(context)
            }
        viewModel.dispatch(ManagePermissionsAction.StatusesChanged(statuses))
    }

    val permissionGate = rememberPermissionGate()
    var settingsRequestPending by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    refreshStatuses()
                    if (settingsRequestPending) {
                        settingsRequestPending = false
                        Mar2AdGateway.completeOriginalOperation()
                    }
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (settingsRequestPending) {
                settingsRequestPending = false
                Mar2AdGateway.completeOriginalOperation()
            }
        }
    }

    LaunchedEffect(viewModel) {
        refreshStatuses()
    }

    CollectEffects(viewModel) { effect ->
        when (effect) {
            ManagePermissionsEvent.NavigateBack -> navigator.back()
            is ManagePermissionsEvent.OpenPermissionSettings ->
                openPermissionSettings(permissionGate, effect.permission) { pending ->
                    settingsRequestPending = pending
                }
        }
    }

    ManagePermissionsScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}

private fun openPermissionSettings(
    gate: PermissionGate,
    permission: AppPermission,
    onPendingChanged: (Boolean) -> Unit,
) {
    onPendingChanged(true)
    Mar2AdGateway.suppressNextForegroundAd()
    if (!gate.openSettings(permission)) {
        onPendingChanged(false)
        Mar2AdGateway.completeOriginalOperation()
    }
}
