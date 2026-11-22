package com.aicleanerstorage.spaceclean.domain.model.junk

/**
 * 一条可清理的垃圾文件。
 *
 * [path] 是扫描时的绝对路径，删除前必须重新校验文件状态；
 * 重复文件的安全信息放在 [duplicate] 中，不参与普通删除判断。
 */
data class JunkFileModel(
    val id: String,
    val path: String,
    val name: String,
    val sizeBytes: Long,
    val category: JunkCategory,
    val modifiedMillis: Long,
    val duplicate: JunkDuplicateMetadataModel? = null,
)
