package com.aicleanerstorage.cleanerkit.files.model

data class FileDeleteResult(
    val outcomes: List<FileDeleteOutcome>,
) {
    val deletedCount: Int = outcomes.count { it.status == DeleteStatus.Deleted }
    val skippedCount: Int get() = outcomes.size - deletedCount
    val freedBytes: Long = outcomes.sumOf { if (it.status == DeleteStatus.Deleted) it.item.sizeBytes else 0L }
}

data class FileDeleteOutcome(
    val item: ManagedFileItem,
    val status: DeleteStatus,
    val authorizationId: String? = null,
)
