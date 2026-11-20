package com.aicleanerstorage.spaceclean.domain.model

/**
 * 濞撳懐鎮婄紒鎾寸亯妫板棗鐓欏Ο鈥崇€?
 */
data class CleanResultModel(
    val cleanedFiles: List<JunkFileModel>,
    val freedSpace: Long,
    val failedFiles: List<JunkFileModel> = emptyList(),
) {
    val successCount: Int
        get() = cleanedFiles.size

    val failedCount: Int
        get() = failedFiles.size
}
