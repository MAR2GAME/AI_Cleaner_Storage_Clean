package com.aicleanerstorage.spaceclean.feature.junkclean.model

data class JunkFile(
    val id: String,
    val filePath: String,
    val fileName: String,
    val fileSize: Long,
    val category: JunkCategory,
    val lastModified: Long = System.currentTimeMillis(),
    val duplicateHash: String? = null,
    val duplicateOriginalPath: String? = null,
) {
    val formattedSize: String
        get() = formatFileSize(fileSize)

    companion object {
        fun formatFileSize(bytes: Long): String =
            when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0)} KB"
                bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024))} MB"
                else -> "${"%.1f".format(bytes / (1024.0 * 1024 * 1024))} GB"
            }
    }
}
