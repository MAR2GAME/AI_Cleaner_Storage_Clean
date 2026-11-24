package com.aicleanerstorage.spaceclean.base.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * 权限申请副作用：页面把要申请的权限放进来，结果通过 onResult 返回。
 *
 * - 运行时权限：系统弹窗；
 * - 特殊权限：跳设置页，返回后重查；
 * - [settingsOnly] 为 true 时只跳设置页，用于系统已经不会再弹的场景。
 *
 * @param request 目标权限；null 表示空闲。
 * @param settingsOnly 是否强制只跳设置页。
 * @param onResult 复检后的权限结果。
 */
@Composable
fun PermissionRequestEffect(
    request: AppPermission?,
    settingsOnly: Boolean = false,
    onResult: (PermissionResult) -> Unit,
) {
    val context = LocalContext.current
    val gate = rememberPermissionGate()
    val latestOnResult by rememberUpdatedState(onResult)
    var awaitingSettings by remember { mutableStateOf<AppPermission?>(null) }

    LaunchedEffect(request, settingsOnly) {
        val permission = request ?: return@LaunchedEffect

        if (gate.isGranted(permission)) {
            latestOnResult(PermissionResult.Granted)
            return@LaunchedEffect
        }

        if (settingsOnly || permission.requiresSettings(context)) {
            awaitingSettings = permission
            if (!gate.openSettings(permission)) {
                awaitingSettings = null
                latestOnResult(PermissionResult.RequiresSettings)
            }
            return@LaunchedEffect
        }

        gate.request(permission) { granted ->
            latestOnResult(
                when {
                    granted -> PermissionResult.Granted
                    permission.shouldExplain(context) -> PermissionResult.Denied
                    else -> PermissionResult.PermanentlyDenied
                },
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event != Lifecycle.Event.ON_RESUME) return@LifecycleEventObserver
                val permission = awaitingSettings ?: return@LifecycleEventObserver
                awaitingSettings = null
                latestOnResult(permission.resultAfterSettings(context))
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
