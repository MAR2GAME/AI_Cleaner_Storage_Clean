package com.aicleanerstorage.spaceclean.base.permission

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/** 应用需要的系统权限；检查、运行时权限与设置页入口集中在这里。 */
enum class AppPermission {
    StorageFiles,
    Location,
    UsageAccess,
    NotificationListener,
    Overlay,
    PostNotifications,
    ;

    fun isGranted(context: Context): Boolean =
        when (this) {
            StorageFiles ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Environment.isExternalStorageManager()
                } else {
                    runtimePermissions(context).all(context::hasPermission)
                }
            Location -> context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            UsageAccess -> isUsageAccessGranted(context)
            NotificationListener ->
                Settings.Secure
                    .getString(context.contentResolver, "enabled_notification_listeners")
                    ?.split(':')
                    ?.any { listener ->
                        android.content.ComponentName
                            .unflattenFromString(listener)
                            ?.packageName == context.packageName
                    } == true
            Overlay -> isOverlayGranted(context)
            PostNotifications ->
                NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

    /** 非空 → 弹系统运行时权限框；空 → 只能跳设置页。 */
    fun runtimePermissions(context: Context): List<String> =
        when (this) {
            StorageFiles ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    emptyList()
                } else {
                    listOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    )
                }
            Location -> listOf(Manifest.permission.ACCESS_FINE_LOCATION)
            PostNotifications ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    emptyList()
                }
            UsageAccess, NotificationListener, Overlay -> emptyList()
        }

    fun settingsIntents(context: Context): List<Intent> =
        when (this) {
            StorageFiles ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    listOf(
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:${context.packageName}"),
                        ),
                        Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),
                        appSettingsAction(context),
                    )
                } else {
                    listOf(appSettingsAction(context))
                }
            Location -> listOf(appSettingsAction(context))
            UsageAccess -> listOf(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), appSettingsAction(context))
            NotificationListener ->
                listOf(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), appSettingsAction(context))
            Overlay ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    listOf(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}"),
                        ),
                        appSettingsAction(context),
                    )
                } else {
                    listOf(appSettingsAction(context))
                }
            PostNotifications -> listOf(appSettingsAction(context))
        }

    /** 特殊权限：没有系统弹窗，只能去设置页。 */
    fun requiresSettings(context: Context): Boolean = runtimePermissions(context).isEmpty()

    /**
     * 是否应该先解释再申请。
     * 只有运行时权限有意义；特殊权限返回 false。
     */
    fun shouldExplain(context: Context): Boolean {
        val activity = context.findActivityOrNull() ?: return false
        return runtimePermissions(context)
            .any { activity.shouldShowRequestPermissionRationale(it) }
    }

    /** 从设置页返回后的结果。 */
    fun resultAfterSettings(context: Context): PermissionResult =
        if (isGranted(context)) PermissionResult.Granted else PermissionResult.RequiresSettings
}

internal fun Context.hasPermission(value: String): Boolean =
    ContextCompat.checkSelfPermission(this, value) == PackageManager.PERMISSION_GRANTED

internal fun appSettingsAction(context: Context): Intent =
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${context.packageName}"),
    )

/** Usage Access 不是运行时权限，只能通过 AppOps 查询。 */
private fun isUsageAccessGranted(context: Context): Boolean =
    runCatching {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val operation = AppOpsManager.OPSTR_GET_USAGE_STATS
        val uid = Process.myUid()
        val mode =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(operation, uid, context.packageName)
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(operation, uid, context.packageName)
            }
        mode == AppOpsManager.MODE_ALLOWED
    }.getOrDefault(false)

/** Overlay 是特殊权限，只能通过 Settings.canDrawOverlays 查询。 */
private fun isOverlayGranted(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
        runCatching { Settings.canDrawOverlays(context) }.getOrDefault(false)
