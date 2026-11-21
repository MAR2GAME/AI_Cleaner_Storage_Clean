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
 * - 是否先解释、是否提示去设置，由页面决定。
 */
@Composable
fun PermissionRequestEffect(
    request: AppPermission?,
    onResult: (PermissionResult) -> Unit,
) {
    val context = LocalContext.current
    val gate = rememberPermissionGate()
    val latestOnResult by rememberUpdatedState(onResult)
    var awaitingSettings by remember { mutableStateOf<AppPermission?>(null) }

    LaunchedEffect(request) {
        val permission = request ?: return@LaunchedEffect

        if (gate.isGranted(permission)) {
            latestOnResult(PermissionResult.Granted)
            return@LaunchedEffect
        }

        if (permission.requiresSettings(context)) {
            awaitingSettings = permission
            gate.openSettings(permission)
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
