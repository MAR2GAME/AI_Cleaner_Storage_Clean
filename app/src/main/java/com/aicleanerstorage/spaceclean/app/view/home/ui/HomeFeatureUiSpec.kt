package com.aicleanerstorage.spaceclean.app.view.home.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

internal data class HomeFeatureUiSpec(
    val destination: AppDestination,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
)

internal val ToolboxTargets =
    listOf(
        HomeFeatureUiSpec(AppDestination.AppUsage, R.string.nav_app_usage, R.drawable.ic_app_usage),
        HomeFeatureUiSpec(AppDestination.NotificationCleaner, R.string.home_notification_bar, R.drawable.ic_notification_cleaner),
        HomeFeatureUiSpec(AppDestination.WhatsAppCleaner, R.string.nav_whatsapp_cleaner, R.drawable.ic_whats_app_cleaner),
        HomeFeatureUiSpec(AppDestination.NetworkUsage, R.string.nav_network_usage, R.drawable.ic_network_usage),
        HomeFeatureUiSpec(AppDestination.NetworkScan, R.string.nav_network_scan, R.drawable.ic_network_scan),
        HomeFeatureUiSpec(AppDestination.NetworkSpeed, R.string.nav_network_speed, R.drawable.ic_network_speed),
    )

internal val FileManagerTargets =
    listOf(
        HomeFeatureUiSpec(AppDestination.PhotosManager, R.string.nav_photos, R.drawable.ic_photos),
        HomeFeatureUiSpec(AppDestination.SimilarPhotosManager, R.string.nav_similar_photos, R.drawable.ic_similar_photos),
        HomeFeatureUiSpec(AppDestination.PhotoPrivacyManager, R.string.nav_photo_privacy, R.drawable.ic_photo_privacy),
        HomeFeatureUiSpec(AppDestination.ScreenshotsManager, R.string.nav_screenshots, R.drawable.ic_screenshots),
        HomeFeatureUiSpec(AppDestination.VideosManager, R.string.nav_videos, R.drawable.ic_videos),
        HomeFeatureUiSpec(AppDestination.AudiosManager, R.string.nav_audios, R.drawable.ic_audios),
        HomeFeatureUiSpec(AppDestination.LargeFilesManager, R.string.nav_large_files, R.drawable.ic_large_files),
        HomeFeatureUiSpec(AppDestination.DuplicateFilesManager, R.string.nav_duplicate_files, R.drawable.ic_duplicate_files),
        HomeFeatureUiSpec(AppDestination.DocumentsManager, R.string.nav_documents, R.drawable.ic_documents),
    )
