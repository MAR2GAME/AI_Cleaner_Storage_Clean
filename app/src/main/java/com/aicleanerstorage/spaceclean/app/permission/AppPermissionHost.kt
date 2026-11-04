package com.aicleanerstorage.spaceclean.app.permission

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.permission.*
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionGateway
import com.aicleanerstorage.spaceclean.common.ui.components.NotificationPermissionDialog
import com.aicleanerstorage.spaceclean.common.ui.permission.StorageCleanPermissionUi
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
    var runtimeOwner by remember(permission) { mutableStateOf<PermissionInterceptor.Request?>(null) }
    var settingsOwner by remember(permission) { mutableStateOf<PermissionInterceptor.Request?>(null) }
    var settingsReturnGeneration by remember { mutableIntStateOf(0) }
    DisposableEffect(permission) {
        onDispose { permission.cancelAll(); onBusy(false) }
    }
    DisposableEffect(lifecycleOwner, permission, externalActivityTracker) {
        val observer = LifecycleEventObserver { _, event ->
            val owner = settingsOwner
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> if (owner != null) permission.markPaused(owner)
                Lifecycle.Event.ON_RESUME -> {
                    externalActivityTracker.markReturn()
                    settingsReturnGeneration++
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(settingsReturnGeneration) {
        val owner = settingsOwner ?: return@LaunchedEffect
        permission.settingsReturned(owner)
        if (permission.request.value?.owner !== owner || owner.stage != PermissionInterceptor.Stage.WaitingSettings) settingsOwner = null
    }
    key(prompt?.owner, prompt?.missing) {
        val callbackOwner = prompt?.owner
        val callbackPermission = prompt?.missing
        val runtimeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val owner = callbackOwner?.takeIf {
                runtimeOwner === it && it.missing == callbackPermission
            }
            if (owner != null) runtimeOwner = null
            val notificationResult = owner?.missing == PermissionType.PostNotifications && owner?.abandoned == false
            if (owner != null && permission.runtimeReturned(owner) && notificationResult) {
                val granted = permission.isGranted(PermissionType.PostNotifications)
                analyticsReporter.report(AnalyticsEvent.NotificationPermissionResult(granted))
                notificationViewModel.onSystemPermissionResult(granted, context.postNotificationRationale())
            }
        }
        val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val owner = callbackOwner?.takeIf {
                settingsOwner === it && it.missing == callbackPermission
            }
            if (owner != null) {
                externalActivityTracker.markReturn()
                permission.settingsReturned(owner, activityResult = true)
                settingsOwner = null
                settingsReturnGeneration++
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
                    permission.launchFailed(owner)
                    return@LaunchedEffect
                }
                if (!runtime && permission.explainSettings(owner)) return@LaunchedEffect
                if (!permission.beginLaunch(owner, runtime)) return@LaunchedEffect
                if (runtime) {
                    runtimeOwner = owner
                    runtimeLauncher.launch(permissions.toTypedArray())
                    permission.runtimeStarted(owner)
                } else {
                    settingsOwner = owner
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
                    if (!launched) { settingsOwner = null; permission.launchFailed(owner) }
                }
            } catch (error: CancellationException) { throw error }
            catch (_: Exception) {
                if (runtimeOwner === owner) runtimeOwner = null
                if (settingsOwner === owner) settingsOwner = null
                permission.launchFailed(owner)
            }
        }
    }
    val showNotification = notification.customDialogVisible && prompt == null
    LaunchedEffect(prompt != null, notification.splashPaused, showNotification) {
        onBusy(prompt != null || notification.splashPaused || showNotification)
    }
    CompositionLocalProvider(LocalPermissionGateway provides permission) {
        content()
        NotificationPermissionEffects(notificationViewModel, currentRoute, permission, onNotificationGranted)
        prompt?.takeIf { it.stage == PermissionInterceptor.Stage.AwaitingUser }?.let {
            StorageCleanPermissionUi.PermissionPrompt(
                PermissionPromptRequest(it.owner.target, it.missing),
                onSubmit = { permission.confirm(it.owner) },
                onDismiss = { permission.dismiss(it.owner) },
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
}
