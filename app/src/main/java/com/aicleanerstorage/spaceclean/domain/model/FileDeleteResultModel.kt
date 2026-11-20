package com.aicleanerstorage.spaceclean.domain.model

data class FileDeleteResultModel(
    val freedBytes: Long,
    val deletedCount: Int,
    val skippedCount: Int = 0,
)
