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
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsEvent
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsAction
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsViewModel

@Composable
fun ManagePermissionsRoute(navigator: AppNavigator, viewModel: ManagePermissionsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val owner = LocalLifecycleOwner.current
    val context = LocalContext.current

    fun refreshStatuses() {
        viewModel.dispatch(
            ManagePermissionsAction.StatusesChanged(
                AppPermission.entries.associateWith { it.isGranted(context) },
            ),
        )
    }

    val permissionGate = rememberPermissionGate()

    DisposableEffect(owner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshStatuses()
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(viewModel) { refreshStatuses() }
    LaunchedEffect(viewModel, navigator) {
        viewModel.event.collect { effect ->
            when (effect) {
                ManagePermissionsEvent.NavigateBack -> navigator.back()
                is ManagePermissionsEvent.OpenSettings -> {
                    permissionGate.request(effect.permission) { refreshStatuses() }
                }
            }
        }
    }
    ManagePermissionsScreen(state = state, onAction = viewModel::dispatch)
}
