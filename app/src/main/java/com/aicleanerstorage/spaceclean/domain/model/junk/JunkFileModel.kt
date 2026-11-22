package com.aicleanerstorage.spaceclean.domain.model.junk

/**
 * 一条可清理的垃圾文件。
 *
 * [path] 是扫描时的绝对路径，删除前必须重新校验文件状态；
 * [duplicateHash] 只用于重复文件分组，不参与删除判断。
 */
data class JunkFileModel(
    val id: String,
    val path: String,
    val name: String,
    val sizeBytes: Long,
    val category: JunkCategory,
    val modifiedMillis: Long = System.currentTimeMillis(),
    val duplicateHash: String? = null,
    val duplicateOriginalPath: String? = null,
)
