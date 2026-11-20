package com.aicleanerstorage.spaceclean.app.view.home.ui

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget

val ToolboxTargets =
    listOf(
        ToolNavigationTarget.AppUsage,
        ToolNavigationTarget.NotificationCleaner,
        ToolNavigationTarget.WhatsAppCleaner,
        ToolNavigationTarget.NetworkUsage,
        ToolNavigationTarget.NetworkScan,
        ToolNavigationTarget.NetworkSpeed,
    )

val FileManagerTargets =
    listOf(
        ToolNavigationTarget.Photos,
        ToolNavigationTarget.SimilarPhotos,
        ToolNavigationTarget.PhotoPrivacy,
        ToolNavigationTarget.Screenshots,
        ToolNavigationTarget.Videos,
        ToolNavigationTarget.Audios,
        ToolNavigationTarget.LargeFiles,
        ToolNavigationTarget.DuplicateFiles,
        ToolNavigationTarget.Documents,
    )

fun ToolNavigationTarget.titleRes(): Int =
    when (this) {
        ToolNavigationTarget.JunkClean -> R.string.home_remove_junk
        ToolNavigationTarget.Antivirus -> R.string.home_virus_title
        ToolNavigationTarget.AppLock -> R.string.home_app_lock_title
        ToolNavigationTarget.DeviceInfo -> R.string.nav_device_info
        ToolNavigationTarget.BatteryInfo -> R.string.nav_battery_info
        ToolNavigationTarget.AppUsage -> R.string.nav_app_usage
        ToolNavigationTarget.NotificationCleaner -> R.string.nav_notification_cleaner
        ToolNavigationTarget.WhatsAppCleaner -> R.string.nav_whatsapp_cleaner
        ToolNavigationTarget.NetworkUsage -> R.string.nav_network_usage
        ToolNavigationTarget.NetworkScan -> R.string.nav_network_scan
        ToolNavigationTarget.NetworkSpeed -> R.string.nav_network_speed
        ToolNavigationTarget.Photos -> R.string.nav_photos
        ToolNavigationTarget.SimilarPhotos -> R.string.nav_similar_photos
        ToolNavigationTarget.PhotoPrivacy -> R.string.nav_photo_privacy
        ToolNavigationTarget.Screenshots -> R.string.nav_screenshots
        ToolNavigationTarget.Videos -> R.string.nav_videos
        ToolNavigationTarget.Audios -> R.string.nav_audios
        ToolNavigationTarget.LargeFiles -> R.string.nav_large_files
        ToolNavigationTarget.DuplicateFiles -> R.string.nav_duplicate_files
        ToolNavigationTarget.Documents -> R.string.nav_documents
        // Toolbox 只是导航目标，不会作为网格条目出现
        ToolNavigationTarget.Toolbox -> R.string.home_toolbox_title
    }

fun ToolNavigationTarget.iconRes(): Int =
    when (this) {
        ToolNavigationTarget.JunkClean -> R.drawable.trash_can
        ToolNavigationTarget.Antivirus -> R.drawable.virus_shield
        ToolNavigationTarget.AppLock -> R.drawable.app_lock
        ToolNavigationTarget.DeviceInfo -> R.drawable.ic_device_phone
        ToolNavigationTarget.BatteryInfo -> R.drawable.ic_battery
        ToolNavigationTarget.AppUsage -> R.drawable.ic_app_usage
        ToolNavigationTarget.NotificationCleaner -> R.drawable.ic_notification_cleaner
        ToolNavigationTarget.WhatsAppCleaner -> R.drawable.ic_whats_app_cleaner
        ToolNavigationTarget.NetworkUsage -> R.drawable.ic_network_usage
        ToolNavigationTarget.NetworkScan -> R.drawable.ic_network_scan
        ToolNavigationTarget.NetworkSpeed -> R.drawable.ic_network_speed
        ToolNavigationTarget.Photos -> R.drawable.ic_photos
        ToolNavigationTarget.SimilarPhotos -> R.drawable.ic_similar_photos
        ToolNavigationTarget.PhotoPrivacy -> R.drawable.ic_photo_privacy
        ToolNavigationTarget.Screenshots -> R.drawable.ic_screenshots
        ToolNavigationTarget.Videos -> R.drawable.ic_videos
        ToolNavigationTarget.Audios -> R.drawable.ic_audios
        ToolNavigationTarget.LargeFiles -> R.drawable.ic_large_files
        ToolNavigationTarget.DuplicateFiles -> R.drawable.ic_duplicate_files
        ToolNavigationTarget.Documents -> R.drawable.ic_documents
        // 同上，网格不会渲染 Toolbox
        ToolNavigationTarget.Toolbox -> R.drawable.ic_persistent_tools
    }
