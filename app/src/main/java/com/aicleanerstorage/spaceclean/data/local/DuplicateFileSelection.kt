package com.aicleanerstorage.spaceclean.data.local

import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import com.aicleanerstorage.spaceclean.domain.rule.DuplicateContentRule

internal object DuplicateFileSelection {
    internal fun canDeleteDuplicate(
        item: ManagedFileItemModel,
        selectedIdentities: Set<String>,
        contentHash: (ManagedFileItemModel) -> String?,
    ): Boolean = DuplicateContentRule.hasVerifiedCopy(
        candidate = item,
        retainedCandidates = item.duplicatePeers.filter { fileIdentity(it) !in selectedIdentities },
        expectedHash = item.duplicateHash,
        identity = ::fileIdentity,
        contentHash = contentHash,
    )

    fun buildDuplicateFileGroups(
        files: List<ManagedFileItemModel>,
        contentHash: (ManagedFileItemModel) -> String?,
    ): List<List<ManagedFileItemModel>> {
        val hashes = mutableMapOf<String, String?>()
        return DuplicateFileGrouping.group(
            candidates = files.map(::duplicateCandidate),
            contentHash = { item -> contentHash(item).also { hashes[fileIdentity(item)] = it } },
        ).map { group ->
            group.map { item ->
                item.copy(
                    duplicateHash = hashes.getValue(fileIdentity(item)),
                    duplicatePeers = group,
                )
            }
        }
    }

    private fun duplicateCandidate(item: ManagedFileItemModel): DuplicateCandidate<ManagedFileItemModel> =
        DuplicateCandidate(
            value = item,
            identity = fileIdentity(item),
            name = item.name,
            sizeBytes = item.sizeBytes,
            modifiedSeconds = item.modifiedSeconds,
        )

    private fun fileIdentity(item: ManagedFileItemModel): String =
        ManagedFileClassifier.identity(item.path, item.uri.value)

}
