package com.aicleanerstorage.spaceclean.domain.model.junk

import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus

/** 单条垃圾文件的删除结果。 */
data class JunkDeleteOutcomeModel(
    val file: JunkFileModel,
    val status: DeleteStatus,
) {
    val deleted: Boolean get() = status == DeleteStatus.Deleted

    val freedBytes: Long get() = if (deleted) file.sizeBytes else 0L
}
