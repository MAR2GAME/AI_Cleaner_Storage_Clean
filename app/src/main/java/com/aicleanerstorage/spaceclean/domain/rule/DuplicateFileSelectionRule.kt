package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileGroupModel
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/**
 * 重复文件的选择与删除安全规则。
 *
 * 把“组哈希、同组副本、当前选中集合”组合成删除前判定；
 * 实际读文件、算哈希由 data 层通过 [contentHash] 提供。
 */
internal object DuplicateFileSelectionRule {
    /**
     * 构造重复文件组。
     *
     * @param files 待分组文件。
     * @param contentHash 内容哈希函数；返回 null 表示读取失败或文件过大。
     * @return 重复文件组；hash 只保存在组上。
     */
    fun buildGroups(
        files: List<ManagedFileModel>,
        contentHash: (ManagedFileModel) -> String?,
    ): List<DuplicateFileGroupModel> =
        DuplicateFileRule.group(files.map(::candidate), contentHash).map { group ->
            DuplicateFileGroupModel(hash = group.hash, files = group.files)
        }

    /**
     * 判断某个重复文件是否可以删除。
     *
     * @param selection 待删除条目及其所属组。
     * @param selectedIdentities 本次批量删除中所有已选中的文件身份。
     * @param contentHash 当前内容哈希函数。
     * @return 仍有未选中的、内容一致的副本时返回 true。
     */
    fun canDelete(
        selection: DuplicateFileSelectionModel,
        selectedIdentities: Set<String>,
        contentHash: (ManagedFileModel) -> String?,
    ): Boolean =
        DuplicateFileRule.hasVerifiedCopy(
            candidate = selection.file,
            retainedCandidates = selection.group.files.filter { fileIdentity(it) !in selectedIdentities },
            expectedHash = selection.group.hash,
            identity = ::fileIdentity,
            contentHash = contentHash,
        )

    private fun candidate(item: ManagedFileModel): DuplicateFileRule.Candidate<ManagedFileModel> =
        DuplicateFileRule.Candidate(
            value = item,
            identity = fileIdentity(item),
            name = item.name,
            sizeBytes = item.sizeBytes,
            modifiedMillis = item.modifiedSeconds * 1_000L,
        )

    private fun fileIdentity(item: ManagedFileModel): String = FileClassificationRule.identity(item.path, item.uri)
}
