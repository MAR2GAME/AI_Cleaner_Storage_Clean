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
 * 通知权限提示：回到首页后延迟展示，每天最多一次；拒绝后回到系统设置。
 *
 * @param enabled 当前是否在首页；不在首页时不弹。
 * @param onActiveChanged 提示是否显示，用于暂停首页的打分/退出提示。
 */
@Composable
fun NotificationPermissionPrompt(
    enabled: Boolean,
    onActiveChanged: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preferences: AppSettingsRepository = koinInject()
    var hasPermission by remember { mutableStateOf(notificationsEnabled(context)) }
    var entered by rememberSaveable { mutableStateOf(false) }
    var showGuide by remember { mutableStateOf(false) }
    var pendingPermission by remember { mutableStateOf<AppPermission?>(null) }

    LaunchedEffect(showGuide) {
        onActiveChanged(showGuide)
    }

    PermissionRequestEffect(pendingPermission) { result ->
        pendingPermission = null
        hasPermission = result == PermissionResult.Granted
        if (result == PermissionResult.PermanentlyDenied || result == PermissionResult.RequiresSettings) {
            openNotificationSettings(context)
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
        showGuide = true
    }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    hasPermission = notificationsEnabled(context)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (showGuide) {
        NotificationPermissionGuideDialog(
            onConfirm = {
                showGuide = false
                pendingPermission = AppPermission.PostNotifications
            },
            onDismiss = { showGuide = false },
        )
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

private fun openNotificationSettings(context: Context) {
    AppPermission.PostNotifications.settingsIntents(context).forEach { intent ->
        try {
            context.startActivity(intent)
            return
        } catch (_: Exception) {
        }
    }
}

private fun notificationsEnabled(context: Context): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()
