package com.aicleanerstorage.spaceclean.data.local.files

import com.aicleanerstorage.spaceclean.data.local.storage.FileContentDigest
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileSelectionRule
import com.aicleanerstorage.spaceclean.domain.rule.FileClassificationRule
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * 重复文件扫描期的哈希缓存。
 *
 * 头部哈希先并行预读：同尺寸文件大多在前 64KB 就能排除，只有头部相同的候选
 * 才整文件读一次全量哈希。同一次扫描内同一个文件不会被读两遍；头部读取已经
 * 到文件末尾时直接复用头部哈希，不再整文件重读。
 *
 * @param readPrefix 头部哈希读取；返回 null 表示读不到。
 * @param readContent 全量内容哈希读取；返回 null 表示读不到。
 */
internal class DuplicateFileHasher(
    private val readPrefix: (ManagedFileModel) -> FileContentDigest.PrefixDigest?,
    private val readContent: (ManagedFileModel) -> String?,
) {
    private val prefixes = HashMap<String, FileContentDigest.PrefixDigest?>()
    private val contents = HashMap<String, String?>()

    /**
     * 并行预读可能重复文件的头部哈希。
     *
     * 只预读大小相同的文件；并发度受限，避免同时抢占存储带宽。
     *
     * @param files 本次扫描的文件全集。
     */
    suspend fun prefetchPrefixes(files: List<ManagedFileModel>) {
        val candidates = DuplicateFileSelectionRule.possibleDuplicateFiles(files)
        if (candidates.size < 2) return
        val permits = Semaphore(PREFETCH_PARALLELISM)
        val digests =
            coroutineScope {
                candidates
                    .chunked(PREFETCH_CHUNK)
                    .flatMap { chunk ->
                        chunk
                            .map { item -> async { item to permits.withPermit { readPrefix(item) } } }
                            .awaitAll()
                    }
            }
        digests.forEach { (item, digest) -> prefixes[identity(item)] = digest }
    }

    fun prefixHash(item: ManagedFileModel): String? = prefixDigest(item)?.hash

    fun contentHash(item: ManagedFileModel): String? {
        val key = identity(item)
        if (contents.containsKey(key)) return contents[key]
        val completePrefix = prefixes[key]?.takeIf { it.isComplete }?.hash
        val value = completePrefix ?: readContent(item)
        contents[key] = value
        return value
    }

    private fun prefixDigest(item: ManagedFileModel): FileContentDigest.PrefixDigest? {
        val key = identity(item)
        if (prefixes.containsKey(key)) return prefixes[key]
        val digest = readPrefix(item)
        prefixes[key] = digest
        return digest
    }

    private fun identity(item: ManagedFileModel): String = FileClassificationRule.identity(item.path, item.uri)

    private companion object {
        const val PREFETCH_PARALLELISM = 3
        const val PREFETCH_CHUNK = 64
    }
}
