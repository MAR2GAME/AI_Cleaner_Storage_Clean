package com.aicleanerstorage.spaceclean.data.local.files

import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/** 单文件删除结果；只在 data 内部使用，对外汇总为 `FileDeleteSummaryModel`。 */
internal data class FileDeleteOutcome(
    val item: ManagedFileModel,
    val status: DeleteStatus,
    val authorizationId: String? = null,
)

/** 一次批删除的逐文件结果。 */
internal data class FileDeleteResult(
    val outcomes: List<FileDeleteOutcome>,
) {
    val deletedCount: Int = outcomes.count { it.status == DeleteStatus.Deleted }

    val skippedCount: Int get() = outcomes.size - deletedCount

    val freedBytes: Long = outcomes.sumOf { if (it.status == DeleteStatus.Deleted) it.item.sizeBytes else 0L }
}

internal fun FileDeleteResult.toSummary(): FileDeleteSummaryModel =
    FileDeleteSummaryModel(
        deletedCount = deletedCount,
        freedBytes = freedBytes,
        skippedCount = skippedCount,
    )
