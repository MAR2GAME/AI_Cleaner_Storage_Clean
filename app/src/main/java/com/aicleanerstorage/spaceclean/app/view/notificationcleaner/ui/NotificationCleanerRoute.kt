package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdPlacementRegistry
import com.aicleanerstorage.spaceclean.app.integration.Mar2CompletionAdEffect
import com.aicleanerstorage.spaceclean.app.view.common.permission.dialogCopy
import com.aicleanerstorage.spaceclean.app.view.common.permission.rememberPermissionPrompt
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerAction
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerEvent
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerPage
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerViewModel
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.NotificationCleanerScreen
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.NotificationPermissionGuideDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialog
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

@Composable
fun NotificationCleanerRoute(
    navigator: AppNavigator,
    viewModel: NotificationCleanerViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    Mar2CompletionAdEffect(
        show = state.page == NotificationCleanerPage.Complete && state.completionResultVisible,
        areaKey = Mar2AdPlacementRegistry.MAIN_NOTIFICATION_CLEAN_FINISH,
        destination = AppDestination.NotificationCleaner,
    )

    val permissionPrompt =
        rememberPermissionPrompt { permission, onConfirm, onDismiss ->
            if (permission == AppPermission.NotificationListener) {
                NotificationPermissionGuideDialog(onConfirm, onDismiss)
            } else {
                PermissionDialog(permission.dialogCopy(), onConfirm, onDismiss)
            }
        }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
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

    CollectEffects(viewModel) { effect ->
        when (effect) {
            NotificationCleanerEvent.Exit -> exitBack()
            is NotificationCleanerEvent.Navigate -> if (!exiting) navigator.navigate(effect.destination)
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
                NotificationCleanerAction.BlockingEnableRequested -> {
                    permissionPrompt.request(AppPermission.NotificationListener) { result ->
                        if (result == PermissionResult.Granted) {
                            viewModel.dispatch(NotificationCleanerAction.BlockingEnableRequested)
                        }
                    }
                }

                is NotificationCleanerAction.NavigationRequested -> if (!exiting) viewModel.dispatch(action)
                else -> viewModel.dispatch(action)
            }
        },
    )
}
