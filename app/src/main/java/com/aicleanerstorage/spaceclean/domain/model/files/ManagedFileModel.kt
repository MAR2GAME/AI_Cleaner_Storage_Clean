package com.aicleanerstorage.spaceclean.domain.model.files

/**
 * 一个由清理页面负责的文件，与产生它的存储介质解耦。
 *
 * 重复文件的分组与删除安全上下文由 `DuplicateFileGroupModel` /
 * `DuplicateFileSelectionModel` 单独表达，不塞进通用文件实体。
 */
data class ManagedFileModel(
    val id: Long,
    val uri: String,
    val path: String?,
    val name: String,
    val sizeBytes: Long,
    val modifiedSeconds: Long,
    val mimeType: String?,
    val bucketName: String?,
    val kind: FileKind,
)
