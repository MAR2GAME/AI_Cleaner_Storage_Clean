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
import androidx.compose.runtime.saveable.rememberSaveable
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
private const val SPLASH_NOTICE_FLAG = 1
private const val HOME_NOTICE_FLAG = 2
private const val CLEANUP_NOTICE_FLAG = 3

internal fun shouldRequestOnSplash(
    onSplash: Boolean,
    attempted: Boolean,
    sdkInt: Int,
): Boolean = onSplash && !attempted && sdkInt >= Build.VERSION_CODES.TIRAMISU

internal fun notificationNoticeFlag(
    onSplash: Boolean,
    hasCompletedCleanup: Boolean,
): Int =
    when {
        onSplash -> SPLASH_NOTICE_FLAG
        hasCompletedCleanup -> CLEANUP_NOTICE_FLAG
        else -> HOME_NOTICE_FLAG
    }

@Composable
internal fun NotificationPermissionPrompt(
    onSplash: Boolean,
    onHome: Boolean,
    coordinator: NotificationPermissionRequestCoordinator,
    onActiveChanged: (Boolean) -> Unit = {},
    hasCompletedCleanup: () -> Boolean = { false },
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val state by coordinator.state.collectAsStateWithLifecycle()
    var showGuide by rememberSaveable { mutableStateOf(false) }
    var guideSourceName by rememberSaveable { mutableStateOf<String?>(null) }
    val guideSource =
        guideSourceName?.let { value ->
            NotificationPermissionRequestSource.entries.firstOrNull { it.name == value }
        }
    LaunchedEffect(showGuide) { onActiveChanged(showGuide) }
    LaunchedEffect(onSplash, onHome) { NotificationUtil.reportNotificationPermission() }

    NotificationPermissionSplashEffect(
        input = NotificationPermissionSplashInput(onSplash, activity, context, state),
        coordinator = coordinator,
        onShowGuide = {
            showGuide = true
            guideSourceName = NotificationPermissionRequestSource.Splash.name
        },
    )
    NotificationPermissionHomeEffect(
        onHome = onHome,
        context = context,
        state = state,
        coordinator = coordinator,
        onShowGuide = {
            showGuide = true
            guideSourceName = NotificationPermissionRequestSource.Home.name
        },
    )
    NotificationPermissionSystemUi(
        input = NotificationPermissionSystemUiInput(state, activity, showGuide, guideSource, hasCompletedCleanup),
        coordinator = coordinator,
        context = context,
        onDismissGuide = { showGuide = false },
        onShowGuide = {
            showGuide = true
            guideSourceName = NotificationPermissionRequestSource.Home.name
        },
    )
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
    val hasCompletedCleanup: () -> Boolean,
)

@Composable
private fun NotificationPermissionSystemUi(
    input: NotificationPermissionSystemUiInput,
    coordinator: NotificationPermissionRequestCoordinator,
    context: Context,
    onDismissGuide: () -> Unit,
    onShowGuide: () -> Unit,
) {
    val runtimeLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            val pending = coordinator.state.value
            val requestId = pending.pendingRequestId
            val source = pending.pendingSource
            NotificationUtil.reportNotificationPermission()
            if (requestId != null && source != null && coordinator.complete(requestId, source)) {
                val noticeFlag =
                    notificationNoticeFlag(
                        onSplash = source == NotificationPermissionRequestSource.Splash,
                        hasCompletedCleanup = input.hasCompletedCleanup(),
                    )
                Mar2Analytics.trackNotificationPopup(noticeFlag, granted)
                Mar2Analytics.trackNotificationPermissionResult(granted)
                if (!granted && source == NotificationPermissionRequestSource.Home) onShowGuide()
            }
        }
    val settingsLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            val granted = notificationAccess(context).hasPermission
            Mar2Analytics.trackNotificationPermissionResult(granted)
            onDismissGuide()
            NotificationUtil.reportNotificationPermission()
            if (input.guideSource == NotificationPermissionRequestSource.Splash) coordinator.releaseSplash()
        }
    NotificationPermissionRequestEffect(input, coordinator, runtimeLauncher, onShowGuide)
    if (input.showGuide) {
        NotificationPermissionGuideDialog(
            onConfirm = {
                Mar2Analytics.trackNotificationPopup(guideNoticeFlag(input), true)
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
                Mar2Analytics.trackNotificationPopup(guideNoticeFlag(input), false)
                onDismissGuide()
                if (input.guideSource == NotificationPermissionRequestSource.Splash) coordinator.releaseSplash()
            },
        )
    }
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
        if (access.needsSettings && access.runtimePermissionGranted) {
            onShowGuide()
        } else {
            coordinator.begin(
                NotificationPermissionRequestSource.Home,
                runtimePermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,
                runtimePermissionGranted = access.runtimePermissionGranted,
            )
        }
    }
}

@Composable
private fun NotificationPermissionRequestEffect(
    input: NotificationPermissionSystemUiInput,
    coordinator: NotificationPermissionRequestCoordinator,
    runtimeLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    onShowGuide: () -> Unit,
) {
    LaunchedEffect(input.state.pendingRequestId, input.state.pendingSource, input.activity) {
        val requestId = input.state.pendingRequestId ?: return@LaunchedEffect
        val host = input.activity ?: return@LaunchedEffect
        val source = input.state.pendingSource ?: return@LaunchedEffect
        NotificationUtil.reqNotiAccess(host, runtimeLauncher) { granted ->
            if (coordinator.complete(requestId, source)) {
                NotificationUtil.reportNotificationPermission()
                val noticeFlag =
                    notificationNoticeFlag(
                        onSplash = source == NotificationPermissionRequestSource.Splash,
                        hasCompletedCleanup = input.hasCompletedCleanup(),
                    )
                Mar2Analytics.trackNotificationPopup(noticeFlag, granted)
                Mar2Analytics.trackNotificationPermissionResult(granted)
                if (!granted && source == NotificationPermissionRequestSource.Home) onShowGuide()
            }
        }
        if (source == NotificationPermissionRequestSource.Splash) {
            delay(SPLASH_PERMISSION_TIMEOUT_MILLIS)
            if (coordinator.state.value.pendingRequestId == requestId) coordinator.releaseSplash()
        }
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

private fun guideNoticeFlag(input: NotificationPermissionSystemUiInput): Int =
    notificationNoticeFlag(
        onSplash = input.guideSource == NotificationPermissionRequestSource.Splash,
        hasCompletedCleanup = input.hasCompletedCleanup(),
    )

private fun notificationAccess(context: Context): com.mar2sdk.core.notify.NotificationAccessState =
    NotificationUtil.getAccessState(context, AppNotificationUtil.getConfiguredChannelIds())
