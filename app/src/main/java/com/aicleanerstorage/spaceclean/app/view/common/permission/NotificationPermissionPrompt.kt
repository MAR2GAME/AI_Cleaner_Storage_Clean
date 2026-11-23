package com.aicleanerstorage.spaceclean.app.view.common.permission

import android.Manifest
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
import com.aicleanerstorage.spaceclean.base.permission.findActivityOrNull
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.util.Calendar

private const val HOME_PROMPT_DELAY_MILLIS = 1_200L

/**
 * 通知权限提示：冷启动闪屏页首次申请，首页延迟兜底，每天最多一次。
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
    val preferences: AppSettingsRepository = koinInject()
    var hasPermission by remember { mutableStateOf(notificationsEnabled(context)) }
    var hasRequestedBefore by remember { mutableStateOf(preferences.readLastNotificationPermissionPromptAt() > 0L) }
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
        if (!viaSettings && result == PermissionResult.PermanentlyDenied && onHome) {
            showGuide = true
        }
    }
    NotificationPermissionSplashEffect(onSplash, hasPermission, hasRequestedBefore) {
        hasRequestedBefore = true
        preferences.saveLastNotificationPermissionPromptAt(System.currentTimeMillis())
        settingsOnly = false
        pendingPermission = AppPermission.PostNotifications
    }

    LaunchedEffect(onHome) {
        if (!onHome || hasPermission) return@LaunchedEffect
        if (!canPromptToday(preferences.readLastNotificationPermissionPromptAt(), System.currentTimeMillis())) {
            return@LaunchedEffect
        }
        delay(HOME_PROMPT_DELAY_MILLIS)
        preferences.saveLastNotificationPermissionPromptAt(System.currentTimeMillis())
        when {
            AppPermission.PostNotifications.requiresSettings(context) -> showGuide = true
            !hasRequestedBefore || shouldShowNotificationRationale(context) -> {
                hasRequestedBefore = true
                settingsOnly = false
                pendingPermission = AppPermission.PostNotifications
            }
            else -> showGuide = true
        }
    }

    NotificationPermissionResumeEffect { granted -> hasPermission = granted }

    NotificationPermissionGuide(
        visible = showGuide,
        onConfirm = {
            showGuide = false
            settingsOnly = true
            pendingPermission = AppPermission.PostNotifications
        },
        onDismiss = { showGuide = false },
    )
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
    hasPermission: Boolean,
    hasRequestedBefore: Boolean,
    onRequest: () -> Unit,
) {
    LaunchedEffect(onSplash) {
        if (!onSplash || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@LaunchedEffect
        if (hasPermission || hasRequestedBefore) return@LaunchedEffect
        onRequest()
    }
}

@Composable
private fun NotificationPermissionGuide(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    NotificationPermissionGuideDialog(onConfirm = onConfirm, onDismiss = onDismiss)
}

private fun shouldShowNotificationRationale(context: Context): Boolean =
    context.findActivityOrNull()?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == true

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

/** 设置引导每天最多一次。 */
internal fun canPromptToday(
    lastPromptAt: Long,
    nowMillis: Long,
): Boolean {
    if (lastPromptAt <= 0L) return true
    val last = Calendar.getInstance().apply { timeInMillis = lastPromptAt }
    val now = Calendar.getInstance().apply { timeInMillis = nowMillis }
    return last.get(Calendar.ERA) != now.get(Calendar.ERA) ||
        last.get(Calendar.YEAR) != now.get(Calendar.YEAR) ||
        last.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR)
}

private fun notificationsEnabled(context: Context): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()
