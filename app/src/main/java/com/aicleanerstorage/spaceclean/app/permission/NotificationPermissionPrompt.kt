package com.aicleanerstorage.spaceclean.app.permission

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aicleanerstorage.spaceclean.app.platform.permission.PermissionPreferences
import com.aicleanerstorage.spaceclean.base.designsystem.component.NotificationPermissionDialog
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate
import java.util.Calendar
import org.koin.compose.koinInject

/**
 * 通知权限：先拉系统框；系统已经弹不出来时才解释并引导去设置页。
 *
 * 「弹不弹得出来」由申请结果直接反推：刚申请过却仍然拿不到
 * `shouldShowRequestPermissionRationale`，只可能是用户选了「不再询问」。
 * 因此不需要记录「是否申请过」。
 */
@Composable
fun NotificationPermissionPrompt(
    onGranted: () -> Unit = {},
    onActiveChanged: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preferences: PermissionPreferences = koinInject()
    val gate = rememberPermissionGate()
    val currentOnGranted by rememberUpdatedState(onGranted)
    val currentOnActiveChanged by rememberUpdatedState(onActiveChanged)
    var enabled by remember { mutableStateOf(notificationsEnabled(context)) }
    var systemUiActive by remember { mutableStateOf(false) }
    var dialogVisible by rememberSaveable { mutableStateOf(false) }
    var entered by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(systemUiActive, dialogVisible) {
        currentOnActiveChanged(systemUiActive || dialogVisible)
    }

    fun promptForSettingsOncePerDay() {
        val now = System.currentTimeMillis()
        if (!canPromptToday(preferences.readLastNotificationPermissionCustomPromptAt(), now)) return
        preferences.saveLastNotificationPermissionCustomPromptAt(now)
        dialogVisible = true
    }

    // 从系统设置页返回后一律复检
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event != Lifecycle.Event.ON_RESUME || enabled) return@LifecycleEventObserver
                if (notificationsEnabled(context)) {
                    enabled = true
                    currentOnGranted()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        if (entered) return@LaunchedEffect
        entered = true
        if (enabled) return@LaunchedEffect
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // 低于 33 没有系统运行时框可弹，只能解释后去设置页
            promptForSettingsOncePerDay()
            return@LaunchedEffect
        }
        systemUiActive = true
        gate.request(AppPermission.PostNotifications) { granted ->
            systemUiActive = false
            enabled = granted
            if (granted) {
                currentOnGranted()
            } else if (!gate.shouldShowRationale(AppPermission.PostNotifications)) {
                // 刚刚申请过仍无 rationale：系统不会再弹，改为解释 + 去设置
                promptForSettingsOncePerDay()
            }
        }
    }

    if (!dialogVisible) return

    NotificationPermissionDialog(
        onConfirm = {
            dialogVisible = false
            gate.openSettings(AppPermission.PostNotifications)
        },
        onDismiss = { dialogVisible = false },
    )
}

/** 设置引导每天最多一次。 */
internal fun canPromptToday(lastPromptAt: Long, nowMillis: Long): Boolean {
    if (lastPromptAt <= 0L) return true
    val last = Calendar.getInstance().apply { timeInMillis = lastPromptAt }
    val now = Calendar.getInstance().apply { timeInMillis = nowMillis }
    return last.get(Calendar.ERA) != now.get(Calendar.ERA) ||
        last.get(Calendar.YEAR) != now.get(Calendar.YEAR) ||
        last.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR)
}

private fun notificationsEnabled(context: Context): Boolean =
    NotificationManagerCompat.from(context).areNotificationsEnabled()
