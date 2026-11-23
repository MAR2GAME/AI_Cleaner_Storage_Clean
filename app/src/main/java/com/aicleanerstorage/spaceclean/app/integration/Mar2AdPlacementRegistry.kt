package com.aicleanerstorage.spaceclean.app.integration

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/** Mar2SDK 广告位名称，与 feature/add-ads-sdk 保持一致。 */
object Mar2AdPlacementRegistry {
    const val OPEN_PAGE = "openPageAdv"
    const val FOREGROUND = "foreGroundAdv"
    const val NEW_GUIDE_SCAN_FINISH = "newGuideScanFinishAdv"
    const val NEW_GUIDE_SKIP = "newGuideSkipAdv"

    private const val MAIN_JUNK_CLEAN = "mainJunkCleanAdv"
    private const val MAIN_FILE_MANAGE = "mainFileManageAdv"
    private const val MAIN_NOTIFICATION_CLEAN = "mainNotificationCleanAdv"
    private const val MAIN_WHATSAPP_CLEAN = "mainWhatsappCleanAdv"
    private const val MAIN_VIRUS_ANTI = "mainVirusAntiAdv"
    private const val MAIN_APP_LOCK = "mainAppLockAdv"
    private const val MAIN_DEVICE_INFO = "mainDeviceInfoAdv"
    private const val MAIN_BATTERY_INFO = "mainBatteryInfoAdv"
    private const val MAIN_APP_USAGE = "mainAppUsageAdv"
    private const val MAIN_NETWORK_USAGE = "mainNetworkUsageAdv"
    private const val MAIN_NETWORK_SCAN = "mainNetworkScanAdv"
    private const val MAIN_NETWORK_SPEED = "mainNetworkSpeedAdv"

    private const val RETURN_JUNK_CLEAN = "returnFromJunkCleanAdv"
    private const val RETURN_FILE_MANAGE = "returnFromFileManageAdv"
    private const val RETURN_NOTIFICATION_CLEAN = "returnFromNotificationCleanAdv"
    private const val RETURN_WHATSAPP_CLEAN = "returnFromWhatsappCleanAdv"
    private const val RETURN_VIRUS_ANTI = "returnFromVirusAntiAdv"
    private const val RETURN_APP_LOCK = "returnFromAppLockAdv"
    private const val RETURN_DEVICE_INFO = "returnFromDeviceInfoAdv"
    private const val RETURN_BATTERY_INFO = "returnFromBatteryInfoAdv"
    private const val RETURN_APP_USAGE = "returnFromAppUsageAdv"
    private const val RETURN_NETWORK_USAGE = "returnFromNetworkUsageAdv"
    private const val RETURN_NETWORK_SCAN = "returnFromNetworkScanAdv"
    private const val RETURN_NETWORK_SPEED = "returnFromNetworkSpeedAdv"

    fun enterArea(destination: AppDestination): String? = when (destination) {
        AppDestination.JunkClean -> MAIN_JUNK_CLEAN
        AppDestination.AntiVirus -> MAIN_VIRUS_ANTI
        AppDestination.AppLock -> MAIN_APP_LOCK
        AppDestination.DeviceInfo -> MAIN_DEVICE_INFO
        AppDestination.BatteryInfo -> MAIN_BATTERY_INFO
        AppDestination.AppUsage -> MAIN_APP_USAGE
        AppDestination.NetworkUsage -> MAIN_NETWORK_USAGE
        AppDestination.NetworkScan -> MAIN_NETWORK_SCAN
        AppDestination.NetworkSpeed -> MAIN_NETWORK_SPEED
        AppDestination.NotificationCleaner -> MAIN_NOTIFICATION_CLEAN
        AppDestination.WhatsAppCleaner -> MAIN_WHATSAPP_CLEAN
        AppDestination.PhotosManager,
        AppDestination.SimilarPhotosManager,
        AppDestination.PhotoPrivacyManager,
        AppDestination.ScreenshotsManager,
        AppDestination.VideosManager,
        AppDestination.AudiosManager,
        AppDestination.LargeFilesManager,
        AppDestination.DuplicateFilesManager,
        AppDestination.DocumentsManager,
        -> MAIN_FILE_MANAGE
        else -> null
    }

    fun returnArea(destination: AppDestination): String? = when (destination) {
        AppDestination.JunkClean -> RETURN_JUNK_CLEAN
        AppDestination.AntiVirus -> RETURN_VIRUS_ANTI
        AppDestination.AppLock -> RETURN_APP_LOCK
        AppDestination.DeviceInfo -> RETURN_DEVICE_INFO
        AppDestination.BatteryInfo -> RETURN_BATTERY_INFO
        AppDestination.AppUsage -> RETURN_APP_USAGE
        AppDestination.NetworkUsage -> RETURN_NETWORK_USAGE
        AppDestination.NetworkScan -> RETURN_NETWORK_SCAN
        AppDestination.NetworkSpeed -> RETURN_NETWORK_SPEED
        AppDestination.NotificationCleaner -> RETURN_NOTIFICATION_CLEAN
        AppDestination.WhatsAppCleaner -> RETURN_WHATSAPP_CLEAN
        AppDestination.PhotosManager,
        AppDestination.SimilarPhotosManager,
        AppDestination.PhotoPrivacyManager,
        AppDestination.ScreenshotsManager,
        AppDestination.VideosManager,
        AppDestination.AudiosManager,
        AppDestination.LargeFilesManager,
        AppDestination.DuplicateFilesManager,
        AppDestination.DocumentsManager,
        -> RETURN_FILE_MANAGE
        else -> null
    }
}
