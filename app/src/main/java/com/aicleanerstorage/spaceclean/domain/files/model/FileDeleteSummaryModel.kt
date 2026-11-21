package com.aicleanerstorage.spaceclean.domain.files.model

/** Outcome of a deletion request, shared by pages that delete managed files. */
data class FileDeleteSummaryModel(
    val deletedCount: Int,
    val freedBytes: Long,
)
