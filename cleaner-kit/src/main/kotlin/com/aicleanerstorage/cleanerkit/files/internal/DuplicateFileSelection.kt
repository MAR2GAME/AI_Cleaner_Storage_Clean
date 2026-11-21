package com.aicleanerstorage.cleanerkit.files.internal

import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.cleanerkit.internal.storage.DuplicateCandidate
import com.aicleanerstorage.cleanerkit.internal.storage.DuplicateContentRule
import com.aicleanerstorage.cleanerkit.internal.storage.DuplicateFileGrouping
import com.aicleanerstorage.cleanerkit.internal.storage.ManagedFileClassifier

internal object DuplicateFileSelection {
    internal fun canDeleteDuplicate(
        item: ManagedFileItem,
        selectedIdentities: Set<String>,
        contentHash: (ManagedFileItem) -> String?,
    ): Boolean = DuplicateContentRule.hasVerifiedCopy(
        candidate = item,
        retainedCandidates = item.duplicatePeers.filter { fileIdentity(it) !in selectedIdentities },
        expectedHash = item.duplicateHash,
        identity = ::fileIdentity,
        contentHash = contentHash,
    )

    fun buildDuplicateFileGroups(
        files: List<ManagedFileItem>,
        contentHash: (ManagedFileItem) -> String?,
    ): List<List<ManagedFileItem>> {
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

    private fun duplicateCandidate(item: ManagedFileItem): DuplicateCandidate<ManagedFileItem> =
        DuplicateCandidate(
            value = item,
            identity = fileIdentity(item),
            name = item.name,
            sizeBytes = item.sizeBytes,
            modifiedMillis = item.modifiedSeconds * 1_000L,
        )

    private fun fileIdentity(item: ManagedFileItem): String =
        ManagedFileClassifier.identity(item.path, item.uri.value)

}
