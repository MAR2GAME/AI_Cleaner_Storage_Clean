package com.aicleanerstorage.spaceclean.app.view.common.permission

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.util.Calendar

private const val HOME_PROMPT_DELAY_MILLIS = 1_200L

/**
 * 通知权限提示：回到首页后延迟处理，每天最多一次。
 *
 * 系统还能弹授权框时直接请求；设置类权限或运行时权限被永久拒绝时，
 * 才展示两步引导并跳设置。
 *
 * @param enabled 当前是否在首页；不在首页时不处理。
 * @param onActiveChanged 是否有系统界面/引导在展示，用于暂停首页的提示。
 */
@Composable
fun NotificationPermissionPrompt(
    enabled: Boolean,
    onActiveChanged: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val preferences: AppSettingsRepository = koinInject()
    var hasPermission by remember { mutableStateOf(notificationsEnabled(context)) }
    var entered by rememberSaveable { mutableStateOf(false) }
    var showGuide by remember { mutableStateOf(false) }
    var pendingPermission by remember { mutableStateOf<AppPermission?>(null) }
    var settingsOnly by remember { mutableStateOf(false) }

    LaunchedEffect(showGuide, pendingPermission) {
        onActiveChanged(showGuide || pendingPermission != null)
    }

    PermissionRequestEffect(pendingPermission, settingsOnly = settingsOnly) { result ->
        val viaSettings = settingsOnly
        pendingPermission = null
        settingsOnly = false
        hasPermission = result == PermissionResult.Granted
        if (!viaSettings && result == PermissionResult.PermanentlyDenied) {
            showGuide = true
        }
    }

    LaunchedEffect(enabled) {
        if (!enabled || entered) return@LaunchedEffect
        entered = true
        if (hasPermission) return@LaunchedEffect
        val now = System.currentTimeMillis()
        if (!canPromptToday(preferences.readLastNotificationPermissionPromptAt(), now)) {
            return@LaunchedEffect
        }
        delay(HOME_PROMPT_DELAY_MILLIS)
        preferences.saveLastNotificationPermissionPromptAt(System.currentTimeMillis())
        if (AppPermission.PostNotifications.requiresSettings(context)) {
            showGuide = true
        } else {
            settingsOnly = false
            pendingPermission = AppPermission.PostNotifications
        }
    }

    NotificationPermissionResumeEffect { granted -> hasPermission = granted }

    if (showGuide) {
        NotificationPermissionGuideDialog(
            onConfirm = {
                showGuide = false
                settingsOnly = true
                pendingPermission = AppPermission.PostNotifications
            },
            onDismiss = { showGuide = false },
        )
    }
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
