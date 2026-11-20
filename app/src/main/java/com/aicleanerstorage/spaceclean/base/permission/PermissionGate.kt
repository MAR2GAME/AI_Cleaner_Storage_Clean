package com.aicleanerstorage.spaceclean.base.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Route 级权限入口：已授权放行 / 拉起系统界面 / 返回后复检。
 *
 * - 授权结论只来自 [AppPermission.isGranted]；launcher 回传值不作数（设置页恒为 CANCELED）。
 * - 同一时刻只处理一个请求，系统本身也只显示一个授权界面。
 * - 不认识业务类型，不持有页面状态；解释文案、弹窗、授权后的动作都由调用方决定。
 */
@Stable
class PermissionGate internal constructor(
    private val hasPermission: (AppPermission) -> Boolean,
    private val rationale: (AppPermission) -> Boolean,
) {
    private var systemUiLauncher: ((AppPermission, () -> Unit) -> Unit)? = null
    private var settingsLauncher: ((AppPermission) -> Unit)? = null
    private var pendingPermission: AppPermission? = null
    private var pendingResult: ((Boolean) -> Unit)? = null

    /** 已授权直接执行 [onGranted]；否则拉起系统界面，复检通过后再执行。 */
    fun allow(permission: AppPermission, onGranted: () -> Unit = {}) {
        if (hasPermission(permission)) {
            onGranted()
            return
        }
        request(permission) { granted -> if (granted) onGranted() }
    }

    /** 无条件拉起（「去管理」场景）。 */
    fun request(permission: AppPermission, onResult: (Boolean) -> Unit = {}) {
        if (pendingPermission != null) return
        pendingPermission = permission
        pendingResult = onResult
        val launcher = systemUiLauncher
        if (launcher == null) finish() else launcher(permission, ::finish)
    }

    /** 只跳设置页，不发起运行时申请；用于系统已经不会再弹的场景。 */
    fun openSettings(permission: AppPermission) {
        settingsLauncher?.invoke(permission)
    }

    /** 是否该先解释再申请；只对运行时权限有意义，设置类权限恒为 false。 */
    fun shouldShowRationale(permission: AppPermission): Boolean = rationale(permission)

    internal fun attach(
        launchSystemUi: (AppPermission, () -> Unit) -> Unit,
        openSettings: (AppPermission) -> Unit,
    ) {
        systemUiLauncher = launchSystemUi
        settingsLauncher = openSettings
    }

    internal fun finish() {
        val permission = pendingPermission ?: return
        val result = pendingResult
        pendingPermission = null
        pendingResult = null
        result?.invoke(hasPermission(permission))
    }
}

@Composable
fun rememberPermissionGate(): PermissionGate {
    val context = LocalContext.current
    val gate =
        remember(context) {
            PermissionGate(
                hasPermission = { permission -> permission.isGranted(context) },
                rationale = { permission -> context.shouldShowRationale(permission) },
            )
        }
    val runtimeLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            // 结果不可信（部分授予、设置页返回、不再询问后立即拒绝），一律复检
            gate.finish()
        }
    val settingsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            gate.finish()
        }
    gate.attach(
        launchSystemUi = { permission, afterSystemUi ->
            permission.launchSystemUi(
                context = context,
                launchRuntime = { values -> runtimeLauncher.launch(values) },
                launchSettings = { intent -> settingsLauncher.launch(intent) },
                onUnavailable = afterSystemUi,
            )
        },
        openSettings = { permission ->
            permission.openSettings(context) { intent -> settingsLauncher.launch(intent) }
        },
    )
    return gate
}

/** 运行时权限优先弹系统框，否则依次尝试设置页；全都拉不起来时立刻回调，避免请求悬空。 */
private fun AppPermission.launchSystemUi(
    context: Context,
    launchRuntime: (Array<String>) -> Unit,
    launchSettings: (Intent) -> Unit,
    onUnavailable: () -> Unit,
) {
    val missingRuntime = runtimePermissions(context).filterNot(context::hasPermission)
    if (missingRuntime.isNotEmpty()) {
        try {
            launchRuntime(missingRuntime.toTypedArray())
        } catch (_: Exception) {
            onUnavailable()
        }
        return
    }
    if (!openSettings(context, launchSettings)) onUnavailable()
}

/** 只走设置页候选 Intent，成功拉起任意一个即返回 true。 */
private fun AppPermission.openSettings(
    context: Context,
    launchSettings: (Intent) -> Unit,
): Boolean {
    settingsIntents(context).forEach { intent ->
        try {
            launchSettings(intent)
            return true
        } catch (_: Exception) {
        }
    }
    return false
}

private fun Context.shouldShowRationale(permission: AppPermission): Boolean {
    val activity = findActivityOrNull() ?: return false
    return permission.runtimePermissions(this)
        .any { value -> activity.shouldShowRequestPermissionRationale(value) }
}

internal tailrec fun Context.findActivityOrNull(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivityOrNull()
        else -> null
    }
