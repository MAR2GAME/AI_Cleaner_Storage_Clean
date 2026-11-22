package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository

/** 应用锁监控是否应该运行的纯规则。 */
object AppLockMonitoringRule {
    /**
     * 判断监控服务是否可以开始检测前台应用。
     *
     * @param repository 应用锁业务端口。
     * @param hasOverlayPermission 是否已授予悬浮窗权限。
     * @param hasUsageAccess 是否已授予使用情况访问权限。
     * @return true 表示已设置 PIN、监控开启、至少锁定一个应用且两项权限都具备。
     */
    fun shouldMonitorApps(
        repository: AppLockRepository,
        hasOverlayPermission: Boolean,
        hasUsageAccess: Boolean,
    ): Boolean =
        repository.isPinSet() &&
            repository.isMonitoringEnabled() &&
            repository.lockedPackages().isNotEmpty() &&
            hasOverlayPermission &&
            hasUsageAccess
}
