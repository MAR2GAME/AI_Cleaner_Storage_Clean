package com.aicleanerstorage.spaceclean.app.integration

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/**
 * 业务埋点里的功能明细值。
 *
 * 路由字符串是类型化导航生成的完整类名（详情页还可能带参数），这里用
 * `AppDestination` 的完整类名做前缀匹配，避免依赖容易碰撞的字符串片段。
 */
internal object AnalyticsDetails {
    fun forRoute(route: String?): String? {
        val value = route ?: return null
        return routes.firstOrNull { value.startsWith(it.routePrefix) }?.details
    }

    fun forDestination(destination: AppDestination): String? =
        when (destination) {
            AppDestination.JunkClean -> JUNK_CLEAN
            AppDestination.AntiVirus,
            AppDestination.VirusQuickScan,
            AppDestination.VirusDeepScan,
            AppDestination.VirusResult,
            AppDestination.NoVirusResult,
            -> VIRUS_ANTI
            AppDestination.AppLock -> APP_LOCK
            AppDestination.DeviceInfo -> DEVICE_INFO
            AppDestination.BatteryInfo -> BATTERY_INFO
            AppDestination.AppUsage -> APP_USAGE
            AppDestination.NotificationCleaner -> NOTIFICATION_CLEAN
            AppDestination.WhatsAppCleaner -> WHATSAPP_CLEAN
            AppDestination.NetworkUsage -> NETWORK_USAGE
            AppDestination.NetworkScan -> NETWORK_SCAN
            AppDestination.NetworkSpeed -> NETWORK_SPEED
            AppDestination.PhotosManager,
            AppDestination.SimilarPhotosManager,
            AppDestination.PhotoPrivacyManager,
            AppDestination.ScreenshotsManager,
            AppDestination.VideosManager,
            AppDestination.AudiosManager,
            AppDestination.LargeFilesManager,
            AppDestination.DuplicateFilesManager,
            AppDestination.DocumentsManager,
            is AppDestination.PhotosDetail,
            is AppDestination.SimilarPhotosDetail,
            is AppDestination.ScreenshotsDetail,
            is AppDestination.VideosDetail,
            is AppDestination.AudiosDetail,
            is AppDestination.LargeFilesDetail,
            is AppDestination.DocumentsDetail,
            -> FILE_MANAGE
            else -> null
        }

    private data class RouteDetails(
        val routePrefix: String,
        val details: String,
    )

    private val routes: List<RouteDetails> by lazy {
        listOf(
            detailsFor(AppDestination.JunkClean, JUNK_CLEAN),
            detailsFor(AppDestination.AntiVirus, VIRUS_ANTI),
            detailsFor(AppDestination.VirusQuickScan, VIRUS_ANTI),
            detailsFor(AppDestination.VirusDeepScan, VIRUS_ANTI),
            detailsFor(AppDestination.VirusResult, VIRUS_ANTI),
            detailsFor(AppDestination.NoVirusResult, VIRUS_ANTI),
            detailsFor(AppDestination.AppLock, APP_LOCK),
            detailsFor(AppDestination.DeviceInfo, DEVICE_INFO),
            detailsFor(AppDestination.BatteryInfo, BATTERY_INFO),
            detailsFor(AppDestination.AppUsage, APP_USAGE),
            detailsFor(AppDestination.NotificationCleaner, NOTIFICATION_CLEAN),
            detailsFor(AppDestination.WhatsAppCleaner, WHATSAPP_CLEAN),
            detailsFor(AppDestination.NetworkUsage, NETWORK_USAGE),
            detailsFor(AppDestination.NetworkScan, NETWORK_SCAN),
            detailsFor(AppDestination.NetworkSpeed, NETWORK_SPEED),
            detailsFor(AppDestination.PhotosManager, FILE_MANAGE),
            detailsFor(AppDestination.SimilarPhotosManager, FILE_MANAGE),
            detailsFor(AppDestination.PhotoPrivacyManager, FILE_MANAGE),
            detailsFor(AppDestination.ScreenshotsManager, FILE_MANAGE),
            detailsFor(AppDestination.VideosManager, FILE_MANAGE),
            detailsFor(AppDestination.AudiosManager, FILE_MANAGE),
            detailsFor(AppDestination.LargeFilesManager, FILE_MANAGE),
            detailsFor(AppDestination.DuplicateFilesManager, FILE_MANAGE),
            detailsFor(AppDestination.DocumentsManager, FILE_MANAGE),
            detailsFor(AppDestination.PhotosDetail(), FILE_MANAGE),
            detailsFor(AppDestination.SimilarPhotosDetail(), FILE_MANAGE),
            detailsFor(AppDestination.ScreenshotsDetail(), FILE_MANAGE),
            detailsFor(AppDestination.VideosDetail(), FILE_MANAGE),
            detailsFor(AppDestination.AudiosDetail(), FILE_MANAGE),
            detailsFor(AppDestination.LargeFilesDetail(), FILE_MANAGE),
            detailsFor(AppDestination.DocumentsDetail(), FILE_MANAGE),
        ).filterNotNull().sortedByDescending { it.routePrefix.length }
    }

    private fun detailsFor(
        destination: AppDestination,
        details: String,
    ): RouteDetails? {
        val prefix = destination::class.qualifiedName ?: return null
        return RouteDetails(prefix, details)
    }

    const val JUNK_CLEAN = "junkClean"
    const val VIRUS_ANTI = "virusAnti"
    const val APP_LOCK = "appLock"
    const val FILE_MANAGE = "fileManage"
    const val DEVICE_INFO = "deviceInfo"
    const val BATTERY_INFO = "batteryInfo"
    const val APP_USAGE = "appUsage"
    const val NOTIFICATION_CLEAN = "notificationClean"
    const val WHATSAPP_CLEAN = "whatsappClean"
    const val NETWORK_USAGE = "networkUsage"
    const val NETWORK_SCAN = "networkScan"
    const val NETWORK_SPEED = "networkSpeed"
}
