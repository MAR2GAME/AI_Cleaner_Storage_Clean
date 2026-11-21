package com.aicleanerstorage.cleanerkit.clean.model

import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus

data class JunkDeleteOutcome(
    val junkFile: JunkFile,
    val status: DeleteStatus,
    val authorizationId: String? = null,
) {
    val deleted: Boolean get() = status == DeleteStatus.Deleted
    val freedBytes: Long get() = if (deleted) junkFile.fileSize else 0L
}
