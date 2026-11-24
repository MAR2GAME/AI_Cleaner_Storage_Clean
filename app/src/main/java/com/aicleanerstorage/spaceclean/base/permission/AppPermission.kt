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

/**
 * 应用需要的系统权限。
 *
 * 这里只描述权限事实和系统入口：是否已授予、需要哪些运行时权限、
 * 申请失败后去哪个设置页。真正的申请副作用在 [PermissionGate] /
 * [PermissionRequestEffect]，data 层只调用 [isGranted] 或 [requireGranted]。
 */
enum class AppPermission {
    /** 文件与媒体访问；Android 11+ 对应全盘访问，Android 10 对应读写权限。 */
    StorageFiles,

    /** 读取照片的 GPS 位置信息；隐私图页面在 StorageFiles 之外单独申请。 */
    MediaLocation,

    /** 精确定位；仅用于 Wi-Fi 局域网扫描。 */
    Location,

    /** 使用情况访问；应用使用统计与 App Lock 需要。 */
    UsageAccess,

    /** 通知监听；通知清理需要。 */
    NotificationListener,

    /** 悬浮窗；App Lock 覆盖层需要。 */
    Overlay,

    /** 通知权限；Android 13+ 的 POST_NOTIFICATIONS。 */
    PostNotifications,
    ;

    /**
     * 查询该权限当前是否已授予。
     *
     * @param context 用于查询系统权限或系统设置的上下文。
     * @return 已授予返回 true；未授予或无法判断返回 false。
     */
    fun isGranted(context: Context): Boolean =
        when (this) {
            StorageFiles ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Environment.isExternalStorageManager()
                } else {
                    runtimePermissions(context).all(context::hasPermission)
                }
            MediaLocation -> context.hasPermission(Manifest.permission.ACCESS_MEDIA_LOCATION)
            Location -> context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            UsageAccess -> isUsageAccessGranted(context)
            NotificationListener -> NotificationListenerAccess.isGranted(context)
            Overlay -> isOverlayGranted(context)
            PostNotifications ->
                NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

    /**
     * 返回需要通过系统运行时弹窗申请的权限。
     *
     * @param context 用于判断 Android 版本的上下文。
     * @return 权限名列表；空列表表示没有运行时弹窗，只能去设置页。
     */
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
            MediaLocation -> listOf(Manifest.permission.ACCESS_MEDIA_LOCATION)
            Location -> listOf(Manifest.permission.ACCESS_FINE_LOCATION)
            PostNotifications ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    emptyList()
                }
            UsageAccess, NotificationListener, Overlay -> emptyList()
        }

    /**
     * 返回可依次尝试的设置页 Intent。
     *
     * @param context 用于拼 package Uri 和包名。
     * @return 按优先级排列的 Intent 列表；调用方逐个尝试，成功拉起一个即可。
     */
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
            MediaLocation -> listOf(appSettingsAction(context))
            Location -> listOf(appSettingsAction(context))
            UsageAccess -> listOf(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), appSettingsAction(context))
            NotificationListener -> notificationListenerSettingsIntents(context)
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
            PostNotifications ->
                listOf(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
                    appSettingsAction(context),
                )
        }

    /**
     * 是否只能通过设置页授予。
     *
     * @param context 用于判断该权限是否有运行时弹窗。
     * @return true 表示 [runtimePermissions] 为空，只能跳设置页。
     */
    fun requiresSettings(context: Context): Boolean = runtimePermissions(context).isEmpty()

    /**
     * 是否应该先展示解释再申请。
     *
     * 只对运行时权限有意义；特殊权限或找不到 Activity 时返回 false。
     *
     * @param context 用于查找宿主 Activity 并查询 shouldShowRequestPermissionRationale。
     * @return true 表示建议先解释再申请。
     */
    fun shouldExplain(context: Context): Boolean {
        val activity = context.findActivityOrNull() ?: return false
        return runtimePermissions(context)
            .any { activity.shouldShowRequestPermissionRationale(it) }
    }

    /**
     * 从设置页返回后重新判断结果。
     *
     * @param context 用于重新查询权限状态。
     * @return 已授予返回 [PermissionResult.Granted]，否则返回 [PermissionResult.RequiresSettings]。
     */
    fun resultAfterSettings(context: Context): PermissionResult {
        if (isGranted(context)) return PermissionResult.Granted
        return PermissionResult.RequiresSettings
    }
}

/**
 * 要求该权限已授予，否则抛出页面可识别的异常。
 *
 * 供 data 层在读取平台数据前做最小断言；页面捕获
 * [PermissionRequiredException] 后按 [PermissionRequiredException.permission]
 * 发起申请或跳设置页。
 *
 * @param context 用于查询权限状态。
 * @throws PermissionRequiredException 权限未授予时抛出。
 */
fun AppPermission.requireGranted(context: Context) {
    if (!isGranted(context)) throw PermissionRequiredException(this)
}

internal fun Context.hasPermission(value: String): Boolean =
    ContextCompat.checkSelfPermission(this, value) == PackageManager.PERMISSION_GRANTED

internal fun appSettingsAction(context: Context): Intent =
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${context.packageName}"),
    )

/**
 * 通知监听只能逐个组件授权：优先直达本应用监听服务的详情页，
 * 系统没有该页面（或 API 30 以下）时回落到通知使用权列表。
 */
private fun notificationListenerSettingsIntents(context: Context): List<Intent> =
    buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            NotificationListenerAccess.ownComponents(context).firstOrNull()?.let { component ->
                add(
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
                        .putExtra(
                            Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                            component.flattenToString(),
                        ),
                )
            }
        }
        add(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        add(appSettingsAction(context))
    }

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
