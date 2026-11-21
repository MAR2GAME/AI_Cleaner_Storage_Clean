package com.aicleanerstorage.cleanerkit.clean.model

import java.util.Locale

data class JunkFile(
    val id: String,
    val filePath: String,
    val fileName: String,
    val fileSize: Long,
    val category: JunkCategory,
    val lastModified: Long = System.currentTimeMillis(),
    val duplicateHash: String? = null,
    val duplicateOriginalPath: String? = null,
    val scanId: String = "",
) {
    val formattedSize: String
        get() = formatFileSize(fileSize)

    companion object {
        fun formatFileSize(bytes: Long): String =
            when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> String.format(Locale.US, "%.1f KB", bytes / 1024.0)
                bytes < 1024L * 1024 * 1024 -> String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024))
                else -> String.format(Locale.US, "%.1f GB", bytes / (1024.0 * 1024 * 1024))
            }
    }
}
