package com.aicleanerstorage.cleanerkit.clean.model

data class ScanResult(
    val junkFiles: List<JunkFile>,
    val sharedStorageAccessLimited: Boolean = false,
) {
    val totalCount: Int get() = junkFiles.size
    val totalSize: Long = junkFiles.sumOf { it.fileSize }
    val categoryBreakdown: Map<JunkCategory, List<JunkFile>> by lazy { junkFiles.groupBy { it.category } }

    companion object {
        val EMPTY = ScanResult(emptyList())
    }
}
