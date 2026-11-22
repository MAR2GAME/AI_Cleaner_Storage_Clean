package com.aicleanerstorage.spaceclean.domain.model.files

/**
 * 一组内容相同的文件。
 *
 * hash 只在组上保存一次；files 就是全部副本，组内顺序按修改时间从新到旧。
 */
data class DuplicateFileGroupModel(
    val hash: String,
    val files: List<ManagedFileModel>,
)
