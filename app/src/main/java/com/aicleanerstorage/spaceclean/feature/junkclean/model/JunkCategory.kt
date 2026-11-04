package com.aicleanerstorage.spaceclean.feature.junkclean.model

enum class JunkCategory(
    val displayName: String,
    val description: String,
) {
    CACHE("Cache Files", "App and system cache"),
    TEMP_FILE("Temp Files", "Temporary files in temp directories"),
    RESIDUAL("Residual Files", "Leftover files from uninstalled apps"),
    APK("APK Files", "Downloaded APK installers"),
    DUPLICATE("Duplicate Files", "Files with duplicate content"),
    LARGE_FILE("Large Files", "Files taking up too much space"),
}
