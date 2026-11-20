package com.aicleanerstorage.spaceclean.base.navigation

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
