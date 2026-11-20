package com.aicleanerstorage.cleanerkit.internal.storage

internal data class DuplicateCandidate<T>(
    val value: T,
    val identity: String,
    val name: String,
    val sizeBytes: Long,
    val modifiedMillis: Long,
)

internal object DuplicateFileGrouping {
    fun <T> group(
        candidates: List<DuplicateCandidate<T>>,
        contentHash: (T) -> String?,
    ): List<List<T>> {
        val uniqueFiles = candidates.filter { it.sizeBytes > 0L }.distinctBy { it.identity }
        val hashGroups = mutableListOf<List<DuplicateCandidate<T>>>()

        uniqueFiles
            .groupBy { it.sizeBytes }
            .values
            .filter { it.size > 1 }
            .forEach { sameSizeFiles ->
                val hashed = sameSizeFiles.map { file -> file to contentHash(file.value) }
                hashed
                    .filter { !it.second.isNullOrBlank() }
                    .groupBy { it.second.orEmpty() }
                    .values
                    .map { group -> group.map { it.first } }
                    .filter { it.size > 1 }
                    .forEach { group -> hashGroups += group.sortedByDescending { it.modifiedMillis } }
            }

        return hashGroups
            .sortedByDescending { group -> group.drop(1).sumOf { it.sizeBytes } }
            .map { group -> group.map { it.value } }
    }
}
