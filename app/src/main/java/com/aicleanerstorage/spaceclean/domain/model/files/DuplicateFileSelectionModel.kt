package com.aicleanerstorage.spaceclean.domain.model.files

/**
 * 重复文件页选中的一个待删除文件，必须携带它所属的组。
 *
 * 删除前用 group.hash 和 group.files 确认仍有未选中的副本。
 */
data class DuplicateFileSelectionModel(
    val file: ManagedFileModel,
    val group: DuplicateFileGroupModel,
)
