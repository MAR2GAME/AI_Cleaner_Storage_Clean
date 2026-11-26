package com.aicleanerstorage.spaceclean.app.view.applock.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockAction
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockEvent
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockPermissions
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockViewModel
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.requiredPermissions
import com.aicleanerstorage.spaceclean.app.view.common.permission.dialogCopy
import com.aicleanerstorage.spaceclean.app.view.common.permission.rememberPermissionPrompt
import com.aicleanerstorage.spaceclean.app.view.common.permission.ui.AppLockOverlayPermissionDialog
import com.aicleanerstorage.spaceclean.app.view.common.permission.ui.AppLockUsagePermissionDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialog
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

@Composable
fun AppLockRoute(
    navigator: AppNavigator,
    viewModel: AppLockViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val toastRes = uiState.toastRes
    val toastMessage = toastRes?.let { stringResource(it) }
    val permissionPrompt =
        rememberPermissionPrompt { permission, onConfirm, onDismiss ->
            when (permission) {
                AppPermission.Overlay -> AppLockOverlayPermissionDialog(onConfirm, onDismiss)
                AppPermission.UsageAccess -> AppLockUsagePermissionDialog(onConfirm, onDismiss)
                else -> PermissionDialog(permission.dialogCopy(), onConfirm, onDismiss)
            }
        }

    fun refreshPermissions() {
        val statuses =
            AppLockPermissions.associateWith { permission ->
                permission.isGranted(context)
            }
        viewModel.dispatch(AppLockAction.StatusesChanged(statuses))
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(AppLockAction.Resumed)
                    refreshPermissions()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel) {
        refreshPermissions()
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            viewModel.dispatch(AppLockAction.ToastShown)
        }
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

    fun dispatchWithPermissions(action: AppLockAction) {
        val missing =
            action.requiredPermissions(uiState.page).filterNot { permission ->
                permission.isGranted(context)
            }
        if (missing.isEmpty()) {
            viewModel.dispatch(action)
            return
        }
        permissionPrompt.requestAll(missing) { granted ->
            refreshPermissions()
            if (granted) viewModel.dispatch(action)
        }
    }

    CollectEffects(viewModel) { effect ->
        when (effect) {
            AppLockEvent.Exit -> exitBack()
            is AppLockEvent.Navigate -> if (!exiting) navigator.navigate(effect.destination)
            is AppLockEvent.RequestPermission ->
                if (!exiting) permissionPrompt.request(effect.permission) { refreshPermissions() }
        }
    }

    AppLockScreen(
        state = uiState,
        onAction = { action ->
            if (exiting) return@AppLockScreen
            when (action) {
                is AppLockAction.PermissionClicked ->
                    permissionPrompt.request(action.permission) { refreshPermissions() }
                else -> dispatchWithPermissions(action)
            }
        },
    )
}
