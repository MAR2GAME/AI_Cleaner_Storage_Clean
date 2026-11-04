package com.aicleanerstorage.spaceclean.common.permission

enum class PermissionType(
    val key: String,
) {
    StorageFiles("storage_files"),
    Location("location"),
    UsageAccess("usage_access"),
    NotificationListener("notification_listener"),
    Overlay("overlay"),
    PostNotifications("post_notifications"),
    ;

    companion object {
        fun fromKey(key: String): PermissionType? =
            entries.firstOrNull { it.key == key }
    }
}
