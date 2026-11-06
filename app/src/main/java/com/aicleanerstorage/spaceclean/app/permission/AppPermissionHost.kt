package com.aicleanerstorage.spaceclean.app.permission

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionPromptRequest

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType

import com.aicleanerstorage.spaceclean.app.platform.permission.PermissionPreferences

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.core.ui.components.NotificationPermissionDialog
import com.aicleanerstorage.spaceclean.core.ui.permission.StorageCleanPermissionUi
import kotlinx.coroutines.CancellationException
import org.koin.compose.koinInject

@Composable
internal fun AppPermissionHost(
    externalActivityTracker: ExternalActivityTracker,
    onPermissionFlowActiveChange: (Boolean) -> Unit,
    notificationViewModel: NotificationPermissionViewModel,
    currentRoute: String?,
    analyticsReporter: AnalyticsReporter,
    onNotificationGranted: () -> Unit,
    content: @Composable () -> Unit,
) {
    val permission: PermissionInterceptor = koinInject()
    val preferences: PermissionPreferences = koinInject()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val prompt by permission.request.collectAsStateWithLifecycle()
    val notification by notificationViewModel.uiState.collectAsStateWithLifecycle()
    val onBusy by rememberUpdatedState(onPermissionFlowActiveChange)
    DisposableEffect(permission) {
        onDispose { permission.cancelAll(); onBusy(false) }
    }
    DisposableEffect(lifecycleOwner, permission, externalActivityTracker) {
        val observer = LifecycleEventObserver { _, event ->
            val current = permission.request.value
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> current?.let(permission::markPaused)
                Lifecycle.Event.ON_RESUME -> {
                    externalActivityTracker.markReturn()
                    current?.let { permission.settingsReturned(it) }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    key(prompt?.owner, prompt?.missing) {
        val callbackPrompt = prompt
        val runtimeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (callbackPrompt != null && permission.runtimeReturned(callbackPrompt) &&
                callbackPrompt.missing == PermissionType.PostNotifications
            ) {
                val granted = permission.isGranted(PermissionType.PostNotifications)
                analyticsReporter.report(AnalyticsEvent.NotificationPermissionResult(granted))
                notificationViewModel.onSystemPermissionResult(granted, context.postNotificationRationale())
            }
        }
        val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (callbackPrompt != null) {
                externalActivityTracker.markReturn()
                permission.settingsReturned(callbackPrompt, activityResult = true)
            }
        }
        LaunchedEffect(prompt) {
            val current = prompt ?: return@LaunchedEffect
            if (current.stage != PermissionInterceptor.Stage.LaunchRequested) return@LaunchedEffect
            val owner = current.owner
            try {
                val permissions = runtimePermissions(current.missing).filterNot { context.hasPermission(it) }
                val runtime = !owner.settingsOnly && permissions.isNotEmpty() && shouldRequestRuntime(context, current.missing, preferences)
                if (!runtime && !owner.settingsOnly && current.missing == PermissionType.PostNotifications) {
                    permission.launchFailed(current)
                    return@LaunchedEffect
                }
                if (!runtime && permission.explainSettings(current)) return@LaunchedEffect
                if (!permission.beginLaunch(current, runtime)) return@LaunchedEffect
                if (runtime) {
                    runtimeLauncher.launch(permissions.toTypedArray())
                    permission.runtimeStarted(current)
                } else {
                    var launched = false
                    for (intent in permissionSettings(context, current.missing).distinctBy { it.toUri(0) }) {
                        try {
                            externalActivityTracker.markLaunch()
                            settingsLauncher.launch(intent)
                            launched = true
                            break
                        } catch (error: CancellationException) {
                            externalActivityTracker.cancelLaunch()
                            throw error
                        } catch (_: Exception) { externalActivityTracker.cancelLaunch() }
                    }
                    if (!launched) permission.launchFailed(current)
                }
            } catch (error: CancellationException) { throw error }
            catch (_: Exception) {
                permission.launchFailed(current)
            }
        }
    }
    val showNotification = notification.customDialogVisible && prompt == null
    LaunchedEffect(prompt != null, notification.splashPaused, showNotification) {
        onBusy(prompt != null || notification.splashPaused || showNotification)
    }
    content()
    NotificationPermissionEffects(notificationViewModel, currentRoute, permission, onNotificationGranted)
    prompt?.takeIf { it.stage == PermissionInterceptor.Stage.AwaitingUser }?.let {
        StorageCleanPermissionUi.PermissionPrompt(
            PermissionPromptRequest(it.owner.target, it.missing),
            onSubmit = { permission.confirm(it) },
            onDismiss = { permission.dismiss(it) },
        )
    }
    if (showNotification) {
        LaunchedEffect(notificationViewModel) { notificationViewModel.onCustomPromptShown() }
        NotificationPermissionDialog(
            onConfirm = notificationViewModel::onCustomPromptConfirmed,
            onDismiss = notificationViewModel::onCustomPromptDismissed,
        )
    }
}
