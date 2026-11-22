package com.aicleanerstorage.spaceclean.domain.model.junk

/** 一次完整扫描的结果。 */
data class JunkScanResultModel(
    val files: List<JunkFileModel>,
) {
    val totalCount: Int get() = files.size

    val totalSize: Long get() = files.sumOf { it.sizeBytes }

    val categoryBreakdown: Map<JunkCategory, List<JunkFileModel>> get() = files.groupBy { it.category }

    companion object {
        val EMPTY = JunkScanResultModel(emptyList())
    }
}
