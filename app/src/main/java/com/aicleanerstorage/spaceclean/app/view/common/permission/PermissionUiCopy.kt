package com.aicleanerstorage.spaceclean.app.view.common.permission

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.permission.AppPermission

internal fun AppPermission.labelRes(): Int =
    when (this) {
        AppPermission.StorageFiles -> R.string.settings_storage_permission
        AppPermission.Location -> R.string.settings_location_permission
        AppPermission.UsageAccess -> R.string.settings_usage_data_permission
        AppPermission.NotificationListener -> R.string.settings_notification_permission
        AppPermission.Overlay -> R.string.app_lock_overlay_permission_title
        AppPermission.PostNotifications -> R.string.settings_post_notifications
    }

@StringRes
internal fun AppPermission.descriptionRes(): Int =
    when (this) {
        AppPermission.StorageFiles -> R.string.permission_storage_files_desc
        AppPermission.Location -> R.string.permission_location_desc
        AppPermission.UsageAccess -> R.string.permission_usage_desc
        AppPermission.NotificationListener -> R.string.permission_notification_desc
        AppPermission.Overlay -> R.string.permission_overlay_desc
        AppPermission.PostNotifications -> R.string.permission_notification_desc
    }
