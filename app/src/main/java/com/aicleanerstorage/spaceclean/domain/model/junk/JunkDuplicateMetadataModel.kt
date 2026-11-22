package com.aicleanerstorage.spaceclean.domain.model.junk

/** 重复文件的安全元数据：内容哈希与保留的原始文件路径。 */
data class JunkDuplicateMetadataModel(
    val hash: String?,
    val originalPath: String,
)
