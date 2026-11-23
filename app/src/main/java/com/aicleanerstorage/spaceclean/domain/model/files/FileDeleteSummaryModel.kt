package com.aicleanerstorage.spaceclean.domain.model.files

/**
 * 一次删除请求的结果，供多个文件页共享。
 *
 * [deletedCount] 与 [freedBytes] 只统计 [DeleteStatus.Deleted]；
 * [skippedCount] 是未删除的数量（不存在、被修改、失败）。
 */
data class FileDeleteSummaryModel(
    val deletedCount: Int,
    val freedBytes: Long,
    val skippedCount: Int = 0,
    val alreadyAbsentCount: Int = 0,
    val isAuthorizationCancelled: Boolean = false,
) {
    val isComplete: Boolean get() = processedCount > 0 && skippedCount == 0 && !isAuthorizationCancelled

    val processedCount: Int get() = deletedCount + alreadyAbsentCount
}
