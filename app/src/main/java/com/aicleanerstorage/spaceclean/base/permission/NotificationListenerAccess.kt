package com.aicleanerstorage.spaceclean.base.permission

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService

/**
 * 本应用通知监听授权的唯一判定口径。
 *
 * 系统把授权记在 `enabled_notification_listeners`，值是「包名/服务类名」，
 * 绑定的单位也是组件而不是包。只比包名会把旧版本遗留的监听组件当成已授权，
 * 造成权限门禁放行、通知清理仍判定无权限；因此这里统一按「清单里真实存在
 * 的本应用监听服务」比对。
 */
object NotificationListenerAccess {
    private const val ENABLED_LISTENERS_KEY = "enabled_notification_listeners"

    /** 系统是否已启用本应用的任意一个通知监听服务。 */
    fun isGranted(context: Context): Boolean =
        isGranted(
            settingValue = Settings.Secure.getString(context.contentResolver, ENABLED_LISTENERS_KEY),
            ownServices = ownComponents(context).mapTo(mutableSetOf()) { it.flattenToString() },
        )

    /** 清单里本应用声明的监听服务；用于判定和跳转按组件定位的设置页。 */
    fun ownComponents(context: Context): List<ComponentName> {
        val intent = Intent(NotificationListenerService.SERVICE_INTERFACE)
        val resolved =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.queryIntentServices(
                    intent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong()),
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.queryIntentServices(intent, PackageManager.MATCH_ALL)
            }
        return resolved
            .mapNotNull { it.serviceInfo }
            .filter { it.packageName == context.packageName }
            .map { ComponentName(it.packageName, it.name) }
            .sortedBy(ComponentName::flattenToString)
    }

    /**
     * 纯函数判定，便于单测覆盖「同包名旧组件」这类历史脏数据。
     *
     * @param settingValue 系统 `enabled_notification_listeners` 原始值。
     * @param ownServices 本应用监听服务，格式 `包名/类名`。
     * @return 授权列表里存在本应用服务时返回 true。
     */
    internal fun isGranted(
        settingValue: String?,
        ownServices: Set<String>,
    ): Boolean =
        ownServices.isNotEmpty() &&
            settingValue
                .orEmpty()
                .split(':')
                .filter { it.isNotBlank() }
                .map(::normalizeComponent)
                .any { it in ownServices }

    /** 把 `包名/.类名` 这类短格式还原成 `包名/全类名`。 */
    private fun normalizeComponent(value: String): String {
        val separator = value.indexOf('/')
        if (separator <= 0 || separator == value.lastIndex) return value
        val packageName = value.substring(0, separator)
        val className = value.substring(separator + 1)
        return if (className.startsWith('.')) "$packageName/$packageName$className" else value
    }
}
