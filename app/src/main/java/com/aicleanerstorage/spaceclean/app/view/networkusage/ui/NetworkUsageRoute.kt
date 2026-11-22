package com.aicleanerstorage.spaceclean.app.view.networkusage.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageAction
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageEvent
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.app.view.networkusage.ui.NetworkUsageRoute
import com.aicleanerstorage.spaceclean.app.view.networkusage.ui.NetworkUsageScreen
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequestEffect
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NetworkUsageRoute(
    navigator: AppNavigator,
    viewModel: NetworkUsageViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    var pendingPermission by remember { mutableStateOf<AppPermission?>(null) }
    PermissionRequestEffect(pendingPermission) { result ->
        pendingPermission = null
        viewModel.dispatch(
            if (result == PermissionResult.Granted) {
                NetworkUsageAction.PermissionGranted
            } else {
                NetworkUsageAction.PermissionRejected
            },
        )
    }
    LaunchedEffect(uiState.permissionRequestPending) {
        if (uiState.permissionRequestPending) {
            viewModel.dispatch(NetworkUsageAction.PermissionRequestConsumed)
            pendingPermission = AppPermission.UsageAccess
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    CollectEffects(viewModel) { effect ->
        when (effect) {
            is NetworkUsageEvent.Exit -> {
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

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(NetworkUsageAction.Resumed)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler { viewModel.dispatch(NetworkUsageAction.BackClicked) }

    NetworkUsageScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}
