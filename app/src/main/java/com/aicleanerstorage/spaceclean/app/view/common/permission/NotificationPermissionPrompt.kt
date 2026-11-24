package com.aicleanerstorage.spaceclean.app.view.common.permission

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.NotificationPermissionGuideDialog
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequestEffect
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult
import com.aicleanerstorage.spaceclean.app.integration.Mar2Analytics
import kotlinx.coroutines.delay

private const val HOME_PROMPT_DELAY_MILLIS = 350L

/**
 * 通知权限提示：每轮冷启动在闪屏页申请一次，到达首页后再申请一次。
 *
 * 系统还能弹授权框时直接请求；设置类权限或运行时权限被永久拒绝时，
 * 才展示两步引导并跳设置。
 *
 * @param onSplash 当前是否在闪屏页。
 * @param onHome 当前是否在首页。
 * @param onActiveChanged 是否有系统界面/引导在展示，用于暂停闪屏或首页提示。
 */
@Composable
fun NotificationPermissionPrompt(
    onSplash: Boolean,
    onHome: Boolean,
    onActiveChanged: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(notificationsEnabled(context)) }
    var splashAttempted by remember { mutableStateOf(false) }
    var homeAttempted by remember { mutableStateOf(false) }
    var showGuide by remember { mutableStateOf(false) }
    var pendingPermission by remember { mutableStateOf<AppPermission?>(null) }
    var settingsOnly by remember { mutableStateOf(false) }
    LaunchedEffect(showGuide, pendingPermission) {
        onActiveChanged(showGuide || pendingPermission != null)
    }
    NotificationPermissionRequestEffect(pendingPermission, settingsOnly, onHome) { result, viaSettings ->
        pendingPermission = null
        settingsOnly = false
        hasPermission = result == PermissionResult.Granted
        reportNotificationPermissionResult(result, viaSettings)
        if (!viaSettings && result == PermissionResult.PermanentlyDenied && onHome) {
            showGuide = true
        }
    }
    NotificationPermissionSplashEffect(onSplash, splashAttempted) {
        splashAttempted = true
        settingsOnly = false
        pendingPermission = AppPermission.PostNotifications
    }

    LaunchedEffect(onHome) {
        if (!onHome) {
            homeAttempted = false
            return@LaunchedEffect
        }
        if (homeAttempted) return@LaunchedEffect
        homeAttempted = true
        delay(HOME_PROMPT_DELAY_MILLIS)
        hasPermission = notificationsEnabled(context)
        if (hasPermission) return@LaunchedEffect
        when {
            AppPermission.PostNotifications.requiresSettings(context) -> showGuide = true
            else -> {
                settingsOnly = false
                pendingPermission = AppPermission.PostNotifications
            }
        }
    }

    NotificationPermissionResumeEffect { granted -> hasPermission = granted }

    val confirmGuide = {
        showGuide = false
        settingsOnly = true
        pendingPermission = AppPermission.PostNotifications
    }
    NotificationPermissionGuide(showGuide, confirmGuide, { showGuide = false })
}

@Composable
private fun NotificationPermissionRequestEffect(
    pending: AppPermission?,
    settingsOnly: Boolean,
    onHome: Boolean,
    onResult: (PermissionResult, Boolean) -> Unit,
) {
    PermissionRequestEffect(pending, settingsOnly = settingsOnly) { result ->
        onResult(result, settingsOnly)
    }
}

@Composable
private fun NotificationPermissionSplashEffect(
    onSplash: Boolean,
    attempted: Boolean,
    onRequest: () -> Unit,
) {
    LaunchedEffect(onSplash) {
        if (shouldRequestOnSplash(onSplash, attempted, Build.VERSION.SDK_INT)) onRequest()
    }
}

internal fun shouldRequestOnSplash(onSplash: Boolean, attempted: Boolean, sdkInt: Int): Boolean =
    onSplash && !attempted && sdkInt >= Build.VERSION_CODES.TIRAMISU

@Composable
private fun NotificationPermissionGuide(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    NotificationPermissionGuideDialog(onConfirm = onConfirm, onDismiss = onDismiss)
}

@Composable
private fun NotificationPermissionResumeEffect(onPermissionChanged: (Boolean) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    onPermissionChanged(notificationsEnabled(context))
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

private fun notificationsEnabled(context: Context): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()

private fun reportNotificationPermissionResult(
    result: PermissionResult,
    viaSettings: Boolean,
) {
    if (!viaSettings && result != PermissionResult.RequiresSettings) {
        Mar2Analytics.trackNotificationPermissionResult(result == PermissionResult.Granted)
    }
}
