package com.aicleanerstorage.spaceclean.domain.model

data class ScanResultModel(
    val junkFiles: List<JunkFileModel>,
    val totalSize: Long,
    val totalCount: Int,
    val categoryBreakdown: Map<JunkCategory, List<JunkFileModel>>,
) {

    companion object {

        val EMPTY =
            ScanResultModel(
                junkFiles = emptyList(),
                totalSize = 0,
                totalCount = 0,
                categoryBreakdown = emptyMap(),
            )
    }
}
