package com.aicleanerstorage.spaceclean.core.navigation

import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey

/** Pure navigation intent used by reusable UI. AppDestination stays at Route boundaries. */
enum class ToolNavigationTarget {
    JunkClean,
    Antivirus,
    Photos,
    Videos,
    Documents,
    AppLock,
    DeviceInfo,
    BatteryInfo,
    NetworkScan,
    NetworkUsage,
    Toolbox,
    AppUsage,
    NetworkSpeed,
    WhatsAppCleaner,
    NotificationCleaner,
    SimilarPhotos,
    Screenshots,
    Audios,
    LargeFiles,
    DuplicateFiles,
    PhotoPrivacy,
}

fun ToolNavigationTarget.toAppDestination(): AppDestination = when (this) {
    ToolNavigationTarget.JunkClean -> AppDestination.JunkClean
    ToolNavigationTarget.Antivirus -> AppDestination.AntiVirus
    ToolNavigationTarget.Photos -> AppDestination.PhotosManager
    ToolNavigationTarget.Videos -> AppDestination.VideosManager
    ToolNavigationTarget.Documents -> AppDestination.DocumentsManager
    ToolNavigationTarget.AppLock -> AppDestination.AppLock
    ToolNavigationTarget.DeviceInfo -> AppDestination.DeviceInfo
    ToolNavigationTarget.BatteryInfo -> AppDestination.BatteryInfo
    ToolNavigationTarget.NetworkScan -> AppDestination.NetworkScan
    ToolNavigationTarget.NetworkUsage -> AppDestination.NetworkUsage
    ToolNavigationTarget.Toolbox -> AppDestination.Main(MainTab.Toolbox)
    ToolNavigationTarget.AppUsage -> AppDestination.AppUsage
    ToolNavigationTarget.NetworkSpeed -> AppDestination.NetworkSpeed
    ToolNavigationTarget.WhatsAppCleaner -> AppDestination.WhatsAppCleaner
    ToolNavigationTarget.NotificationCleaner -> AppDestination.NotificationCleaner
    ToolNavigationTarget.SimilarPhotos -> AppDestination.SimilarPhotosManager
    ToolNavigationTarget.Screenshots -> AppDestination.ScreenshotsManager
    ToolNavigationTarget.Audios -> AppDestination.AudiosManager
    ToolNavigationTarget.LargeFiles -> AppDestination.LargeFilesManager
    ToolNavigationTarget.DuplicateFiles -> AppDestination.DuplicateFilesManager
    ToolNavigationTarget.PhotoPrivacy -> AppDestination.PhotoPrivacyManager
}

val ToolNavigationTarget.featureKey: FeatureKey?
    get() = when (this) {
        ToolNavigationTarget.JunkClean -> FeatureKey.JUNK_CLEAN
        ToolNavigationTarget.Antivirus -> FeatureKey.ANTI_VIRUS
        ToolNavigationTarget.Photos -> FeatureKey.PHOTOS
        ToolNavigationTarget.Videos -> FeatureKey.VIDEOS
        ToolNavigationTarget.Documents -> FeatureKey.DOCUMENTS
        ToolNavigationTarget.AppLock -> FeatureKey.APP_LOCK
        ToolNavigationTarget.DeviceInfo -> FeatureKey.DEVICE_INFO
        ToolNavigationTarget.BatteryInfo -> FeatureKey.BATTERY_INFO
        ToolNavigationTarget.NetworkScan -> FeatureKey.NETWORK_SCAN
        ToolNavigationTarget.NetworkUsage -> FeatureKey.NETWORK_USAGE
        ToolNavigationTarget.AppUsage -> FeatureKey.APP_USAGE
        ToolNavigationTarget.NetworkSpeed -> FeatureKey.NETWORK_SPEED
        ToolNavigationTarget.WhatsAppCleaner -> FeatureKey.WHATSAPP_CLEANER
        ToolNavigationTarget.NotificationCleaner -> FeatureKey.NOTIFICATION_CLEANER
        ToolNavigationTarget.SimilarPhotos -> FeatureKey.SIMILAR_PHOTOS
        ToolNavigationTarget.Screenshots -> FeatureKey.SCREENSHOTS
        ToolNavigationTarget.Audios -> FeatureKey.AUDIOS
        ToolNavigationTarget.LargeFiles -> FeatureKey.LARGE_FILES
        ToolNavigationTarget.DuplicateFiles -> FeatureKey.DUPLICATE_FILES
        ToolNavigationTarget.PhotoPrivacy -> FeatureKey.PHOTO_PRIVACY
        ToolNavigationTarget.Toolbox -> null
    }

fun FeatureKey.toToolNavigationTarget(): ToolNavigationTarget? = when (this) {
    FeatureKey.JUNK_CLEAN -> ToolNavigationTarget.JunkClean
    FeatureKey.ANTI_VIRUS -> ToolNavigationTarget.Antivirus
    FeatureKey.APP_LOCK -> ToolNavigationTarget.AppLock
    FeatureKey.DEVICE_INFO -> ToolNavigationTarget.DeviceInfo
    FeatureKey.BATTERY_INFO -> ToolNavigationTarget.BatteryInfo
    FeatureKey.APP_USAGE -> ToolNavigationTarget.AppUsage
    FeatureKey.NOTIFICATION_CLEANER -> ToolNavigationTarget.NotificationCleaner
    FeatureKey.WHATSAPP_CLEANER -> ToolNavigationTarget.WhatsAppCleaner
    FeatureKey.NETWORK_USAGE -> ToolNavigationTarget.NetworkUsage
    FeatureKey.NETWORK_SCAN -> ToolNavigationTarget.NetworkScan
    FeatureKey.NETWORK_SPEED -> ToolNavigationTarget.NetworkSpeed
    FeatureKey.PHOTOS -> ToolNavigationTarget.Photos
    FeatureKey.SIMILAR_PHOTOS -> ToolNavigationTarget.SimilarPhotos
    FeatureKey.PHOTO_PRIVACY -> ToolNavigationTarget.PhotoPrivacy
    FeatureKey.SCREENSHOTS -> ToolNavigationTarget.Screenshots
    FeatureKey.VIDEOS -> ToolNavigationTarget.Videos
    FeatureKey.AUDIOS -> ToolNavigationTarget.Audios
    FeatureKey.LARGE_FILES -> ToolNavigationTarget.LargeFiles
    FeatureKey.DUPLICATE_FILES -> ToolNavigationTarget.DuplicateFiles
    FeatureKey.DOCUMENTS -> ToolNavigationTarget.Documents
}
