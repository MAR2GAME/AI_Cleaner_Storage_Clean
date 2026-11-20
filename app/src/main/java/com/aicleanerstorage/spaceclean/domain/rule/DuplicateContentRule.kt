package com.aicleanerstorage.spaceclean.domain.rule

object DuplicateContentRule {
    fun <T> hasVerifiedCopy(
        candidate: T,
        retainedCandidates: List<T>,
        expectedHash: String?,
        identity: (T) -> String,
        contentHash: (T) -> String?,
    ): Boolean {
        if (expectedHash.isNullOrBlank()) return false
        val candidateId = identity(candidate)
        if (contentHash(candidate) != expectedHash) return false
        return retainedCandidates.any { retained ->
            identity(retained) != candidateId && contentHash(retained) == expectedHash
        }
    }
}
