package com.aicleanerstorage.spaceclean.app.view.common.permission

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import com.aicleanerstorage.spaceclean.app.view.common.permission.ui.PostNotificationsPermissionDialog
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequestEffect
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult
import com.aicleanerstorage.spaceclean.domain.settings.port.SettingsRepository
import org.koin.compose.koinInject
import java.util.Calendar

/**
 * 通知权限提示：先解释，再拉系统弹窗 / 设置页；每天最多一次。
 */
@Composable
fun NotificationPermissionPrompt(onActiveChanged: (Boolean) -> Unit = {}) {
    val context = LocalContext.current
    val preferences: SettingsRepository = koinInject()
    var enabled by remember { mutableStateOf(notificationsEnabled(context)) }
    var entered by rememberSaveable { mutableStateOf(false) }
    var showExplain by remember { mutableStateOf(false) }
    var pendingPermission by remember { mutableStateOf<AppPermission?>(null) }

    LaunchedEffect(showExplain) {
        onActiveChanged(showExplain)
    }

    PermissionRequestEffect(pendingPermission) { result ->
        pendingPermission = null
        enabled = result == PermissionResult.Granted
    }

    LaunchedEffect(Unit) {
        if (entered) return@LaunchedEffect
        entered = true
        if (enabled) return@LaunchedEffect

        val now = System.currentTimeMillis()
        if (!canPromptToday(preferences.readLastNotificationPermissionPromptAt(), now)) {
            return@LaunchedEffect
        }
        preferences.saveLastNotificationPermissionPromptAt(now)
        showExplain = true
    }

    if (showExplain) {
        PostNotificationsPermissionDialog(
            onConfirm = {
                showExplain = false
                pendingPermission = AppPermission.PostNotifications
            },
            onDismiss = { showExplain = false },
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

private fun notificationsEnabled(context: Context): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()
