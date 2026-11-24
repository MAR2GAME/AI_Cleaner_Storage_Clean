package com.aicleanerstorage.spaceclean.app.view.common.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.integration.Mar2Analytics
import com.aicleanerstorage.spaceclean.app.integration.NotificationPermissionRequestCoordinator
import com.aicleanerstorage.spaceclean.app.integration.NotificationPermissionRequestSource
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.NotificationPermissionGuideDialog
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.mar2sdk.core.notify.NotificationUtil
import com.mar2sdk.core.notify.app.AppNotificationUtil
import kotlinx.coroutines.delay

private const val HOME_PROMPT_DELAY_MILLIS = 350L
private const val SPLASH_PERMISSION_TIMEOUT_MILLIS = 5_000L

internal fun shouldRequestOnSplash(
    onSplash: Boolean,
    attempted: Boolean,
    sdkInt: Int,
): Boolean = onSplash && !attempted && sdkInt >= Build.VERSION_CODES.TIRAMISU

@Composable
internal fun NotificationPermissionPrompt(
    onSplash: Boolean,
    onHome: Boolean,
    coordinator: NotificationPermissionRequestCoordinator,
    onActiveChanged: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val state by coordinator.state.collectAsStateWithLifecycle()
    var showGuide by remember { mutableStateOf(false) }
    var guideSource by remember { mutableStateOf<NotificationPermissionRequestSource?>(null) }
    LaunchedEffect(showGuide) { onActiveChanged(showGuide) }
    LaunchedEffect(onSplash, onHome) { NotificationUtil.reportNotificationPermission() }

    NotificationPermissionSplashEffect(
        input = NotificationPermissionSplashInput(onSplash, activity, context, state),
        coordinator = coordinator,
        onShowGuide = {
            showGuide = true
            guideSource = NotificationPermissionRequestSource.Splash
        },
    )
    NotificationPermissionHomeEffect(
        onHome = onHome,
        context = context,
        state = state,
        coordinator = coordinator,
        onShowGuide = {
            showGuide = true
            guideSource = NotificationPermissionRequestSource.Home
        },
    )
    NotificationPermissionSystemUi(
        input = NotificationPermissionSystemUiInput(state, activity, showGuide, guideSource),
        coordinator = coordinator,
        context = context,
        onDismissGuide = { showGuide = false },
        onShowGuide = {
            showGuide = true
            guideSource = NotificationPermissionRequestSource.Home
        },
    )
}

private fun notificationAccess(context: Context): com.mar2sdk.core.notify.NotificationAccessState {
    return NotificationUtil.getAccessState(context, AppNotificationUtil.getConfiguredChannelIds())
}

private data class NotificationPermissionSplashInput(
    val onSplash: Boolean,
    val activity: Activity?,
    val context: Context,
    val state: com.aicleanerstorage.spaceclean.app.integration.NotificationPermissionRequestState,
)

private data class NotificationPermissionSystemUiInput(
    val state: com.aicleanerstorage.spaceclean.app.integration.NotificationPermissionRequestState,
    val activity: Activity?,
    val showGuide: Boolean,
    val guideSource: NotificationPermissionRequestSource?,
)

@Composable
private fun NotificationPermissionSystemUi(
    input: NotificationPermissionSystemUiInput,
    coordinator: NotificationPermissionRequestCoordinator,
    context: Context,
    onDismissGuide: () -> Unit,
    onShowGuide: () -> Unit,
) {
    val runtimeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val source = coordinator.state.value.pendingSource
        coordinator.complete(coordinator.state.value.pendingRequestId, source ?: NotificationPermissionRequestSource.Home)
        NotificationUtil.reportNotificationPermission()
        Mar2Analytics.trackNotificationPermissionResult(granted)
        if (!granted && source == NotificationPermissionRequestSource.Home) onShowGuide()
    }
    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        onDismissGuide()
        NotificationUtil.reportNotificationPermission()
        if (input.guideSource == NotificationPermissionRequestSource.Splash) coordinator.releaseSplash()
    }
    NotificationPermissionRequestEffect(input.state, input.activity, coordinator, runtimeLauncher, onShowGuide)
    if (input.showGuide) NotificationPermissionGuideDialog(
        onConfirm = {
            onDismissGuide()
            AppPermission.PostNotifications.settingsIntents(context).firstOrNull()?.let {
                runCatching { settingsLauncher.launch(it) }
                    .onFailure {
                        if (input.guideSource == NotificationPermissionRequestSource.Splash) {
                            coordinator.releaseSplash()
                        }
                    }
            }
        },
        onDismiss = {
            onDismissGuide()
            if (input.guideSource == NotificationPermissionRequestSource.Splash) coordinator.releaseSplash()
        },
    )
}

@Composable
private fun NotificationPermissionSplashEffect(
    input: NotificationPermissionSplashInput,
    coordinator: NotificationPermissionRequestCoordinator,
    onShowGuide: () -> Unit,
) {
    LaunchedEffect(input.onSplash, input.activity, input.state.splashRequestUsed, input.state.splashReleased) {
        if (!input.onSplash || input.activity == null || input.state.splashReleased) return@LaunchedEffect
        val access = notificationAccess(input.context)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || access.hasPermission) {
            coordinator.releaseSplash()
        } else if (access.needsSettings && access.runtimePermissionGranted) {
            onShowGuide()
        } else if (coordinator.begin(
                NotificationPermissionRequestSource.Splash,
                runtimePermissionRequired = true,
                runtimePermissionGranted = access.runtimePermissionGranted,
            ) == null
        ) {
            coordinator.releaseSplash()
        }
    }
}

@Composable
private fun NotificationPermissionHomeEffect(
    onHome: Boolean,
    context: Context,
    state: com.aicleanerstorage.spaceclean.app.integration.NotificationPermissionRequestState,
    coordinator: NotificationPermissionRequestCoordinator,
    onShowGuide: () -> Unit,
) {
    LaunchedEffect(onHome, state.pendingRequestId, state.homeRequestUsed) {
        if (!onHome || state.homeRequestUsed || state.pendingRequestId != null) return@LaunchedEffect
        delay(HOME_PROMPT_DELAY_MILLIS)
        val access = notificationAccess(context)
        if (access.hasPermission) return@LaunchedEffect
        if (access.needsSettings && access.runtimePermissionGranted) onShowGuide() else coordinator.begin(
            NotificationPermissionRequestSource.Home,
            runtimePermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,
            runtimePermissionGranted = access.runtimePermissionGranted,
        )
    }
}

@Composable
private fun NotificationPermissionRequestEffect(
    state: com.aicleanerstorage.spaceclean.app.integration.NotificationPermissionRequestState,
    activity: Activity?,
    coordinator: NotificationPermissionRequestCoordinator,
    runtimeLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    onShowGuide: () -> Unit,
) {
    LaunchedEffect(state.pendingRequestId, state.pendingSource, activity) {
        val requestId = state.pendingRequestId ?: return@LaunchedEffect
        val host = activity ?: return@LaunchedEffect
        val source = state.pendingSource ?: return@LaunchedEffect
        NotificationUtil.reqNotiAccess(host, runtimeLauncher) { granted ->
            coordinator.complete(requestId, source)
            NotificationUtil.reportNotificationPermission()
            Mar2Analytics.trackNotificationPermissionResult(granted)
            if (!granted && source == NotificationPermissionRequestSource.Home) onShowGuide()
        }
        if (source == NotificationPermissionRequestSource.Splash) {
            delay(SPLASH_PERMISSION_TIMEOUT_MILLIS)
            if (coordinator.state.value.pendingRequestId == requestId) coordinator.releaseSplash()
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
