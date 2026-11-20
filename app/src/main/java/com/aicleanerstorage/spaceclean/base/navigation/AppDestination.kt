package com.aicleanerstorage.spaceclean.base.navigation

import androidx.annotation.Keep
import kotlinx.serialization.Serializable


/** 类型化导航目的地；只表达“去哪里”，不携带路由字符串、广告或通知元数据。 */
@Keep
@Serializable
sealed interface AppDestination {
    @Serializable data object Splash : AppDestination

    @Serializable data object OnboardingScan : AppDestination

    @Serializable data class Main(val initialTab: MainTab = MainTab.Home) : AppDestination

    @Serializable data object Settings : AppDestination

    @Serializable data object ManagePermissions : AppDestination

    @Serializable data object JunkClean : AppDestination

    @Serializable data object AntiVirus : AppDestination

    @Serializable data object VirusQuickScan : AppDestination

    @Serializable data object VirusDeepScan : AppDestination

    @Serializable data object VirusResult : AppDestination

    @Serializable data object NoVirusResult : AppDestination

    @Serializable data object AppLock : AppDestination

    @Serializable data object DeviceInfo : AppDestination

    @Serializable data object BatteryInfo : AppDestination

    @Serializable data object AppUsage : AppDestination

    @Serializable data object NetworkUsage : AppDestination

    @Serializable data object NetworkScan : AppDestination

    @Serializable data object NetworkScanDevices : AppDestination

    @Serializable data object NetworkSpeed : AppDestination

    @Serializable data object WhatsAppCleaner : AppDestination

    @Serializable data object NotificationCleaner : AppDestination

    @Serializable data object PhotosManager : AppDestination

    @Serializable data object SimilarPhotosManager : AppDestination

    @Serializable data object PhotoPrivacyManager : AppDestination

    @Serializable data object ScreenshotsManager : AppDestination

    @Serializable data object VideosManager : AppDestination

    @Serializable data object AudiosManager : AppDestination

    @Serializable data object LargeFilesManager : AppDestination

    @Serializable data object DuplicateFilesManager : AppDestination

    @Serializable data object DocumentsManager : AppDestination

    @Serializable data class PhotosDetail(val initialIndex: Int = 0) : AppDestination

    @Serializable data class SimilarPhotosDetail(val initialIndex: Int = 0) : AppDestination

    @Serializable data class ScreenshotsDetail(val initialIndex: Int = 0) : AppDestination

    @Serializable data class VideosDetail(val initialIndex: Int = 0) : AppDestination

    @Serializable data class AudiosDetail(val initialIndex: Int = 0) : AppDestination

    @Serializable data class LargeFilesDetail(val initialIndex: Int = 0) : AppDestination

    @Serializable data class DocumentsDetail(val initialIndex: Int = 0) : AppDestination
}
