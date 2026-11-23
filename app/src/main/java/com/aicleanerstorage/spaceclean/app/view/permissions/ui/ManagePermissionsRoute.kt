package com.aicleanerstorage.spaceclean.app.view.permissions.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.common.permission.rememberPermissionPrompt
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsAction
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsEvent
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsViewModel
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission

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

    val permissionPrompt = rememberPermissionPrompt()

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) refreshStatuses()
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel) {
        refreshStatuses()
    }

    CollectEffects(viewModel) { effect ->
        when (effect) {
            ManagePermissionsEvent.NavigateBack -> navigator.back()
            is ManagePermissionsEvent.OpenPermissionSettings ->
                permissionPrompt.request(effect.permission) { refreshStatuses() }
        }
    }

    ManagePermissionsScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}
