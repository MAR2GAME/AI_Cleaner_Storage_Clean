package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.appusage.AppUsageInfoModel

/**
 * 应用使用统计查询契约：使用时长、运行状态和前台应用。
 *
 * 查询依赖系统的使用情况访问权限；权限缺失时返回空结果，由页面负责引导授权。
 */
interface AppUsageRepository {
    /**
     * 判断是否拥有使用情况访问权限。
     *
     * @param refresh true 表示跳过缓存重新读取系统状态。
     * @return true 表示可以查询使用统计。
     */
    fun hasAppUsageAccess(refresh: Boolean = false): Boolean

    /**
     * 查询指定时间区间内的应用使用统计。
     *
     * @param startMillis 区间起点时间戳，包含。
     * @param endMillis 区间终点时间戳，包含。
     * @return 按使用时长、包名排序的应用统计；无权限或区间非法时为空列表。
     */
    suspend fun appUsageBetween(
        startMillis: Long,
        endMillis: Long,
    ): List<AppUsageInfoModel>

    /**
     * 查询指定包中当前正在运行的应用。
     *
     * @param packageNames 待检查的包名集合。
     * @return 正在运行的包名子集；无权限时为空集合。
     */
    suspend fun runningPackages(packageNames: Set<String>): Set<String>

    /**
     * 查询当前前台应用包名。
     *
     * @return 前台包名；权限缺失或系统没有返回时返回 null。
     */
    suspend fun foregroundPackage(): String?
}
