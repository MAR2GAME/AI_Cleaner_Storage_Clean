package com.aicleanerstorage.spaceclean.domain.rule

/**
 * 重复文件规则。
 *
 * 只做“同大小 + 同哈希 + 至少两份”的判断；哈希与文件身份由调用方以
 * 函数参数传入，因此规则本身不读磁盘，也不依赖 Android。
 */
internal object DuplicateFileRule {
    /**
     * 分组输入。
     *
     * 用泛型 [T] 让同一套规则既能处理 `ManagedFileModel`，也能处理扫描器的
     * 轻量文件模型，避免为每个模型复制一份算法。
     *
     * @property value 原始文件对象。
     * @property identity 去重身份。
     * @property name 文件名，用于稳定排序。
     * @property sizeBytes 文件大小，用于第一层筛选。
     * @property modifiedMillis 修改时间，用于组内排序。
     */
    data class Candidate<T>(
        val value: T,
        val identity: String,
        val name: String,
        val sizeBytes: Long,
        val modifiedMillis: Long,
    )

    data class Group<T>(
        val hash: String?,
        val files: List<T>,
    )

    fun <T> group(
        candidates: List<Candidate<T>>,
        contentHash: (T) -> String?,
    ): List<Group<T>> =
        candidates
            .filter { it.sizeBytes > 0 }
            .distinctBy { it.identity }
            .groupBy { it.sizeBytes }
            .values
            .filter { it.size > 1 }
            .flatMap { groupSameSize(it.sortedByDescending { candidate -> candidate.modifiedMillis }, contentHash) }
            .sortedByDescending { it.files.first().sizeBytes * (it.files.size - 1) }
            .map { group -> Group(group.hash, group.files.map { it.value }) }

    private fun <T> groupSameSize(
        candidates: List<Candidate<T>>,
        contentHash: (T) -> String?,
    ): List<Group<Candidate<T>>> {
        val hashes = candidates.associateWith { contentHash(it.value)?.takeIf(String::isNotBlank) }
        val groups = mutableListOf<Group<Candidate<T>>>()
        for ((hash, files) in candidates.groupBy(hashes::get)) {
            if (hash != null && files.size > 1) groups += Group(hash, files)
        }
        for (names in candidates.groupBy { it.name.lowercase() }.values) {
            val unreadable = names.filter { hashes[it] == null }
            if (unreadable.isEmpty()) continue
            val retained = names.firstOrNull { hashes[it] != null }
            val possible = listOfNotNull(retained) + unreadable
            if (possible.size > 1) groups += Group(null, possible)
        }
        return groups
    }

    /**
     * 判断候选文件是否还有一份未选中的、内容相同的副本。
     *
     * 删除前必须同时确认：候选文件自身内容与扫描时一致，且保留副本内容也一致；
     * 否则用户可能把最后一份有效文件删掉。
     *
     * @param candidate 待删除文件。
     * @param retainedCandidates 未参与删除的副本。
     * @param expectedHash 扫描时记录的哈希。
     * @param identity 文件身份函数。
     * @param contentHash 当前内容哈希函数。
     * @return 存在可保留的相同副本时返回 true。
     */
    fun <T> hasVerifiedCopy(
        candidate: T,
        retainedCandidates: List<T>,
        expectedHash: String?,
        identity: (T) -> String,
        contentHash: (T) -> String?,
    ): Boolean {
        val currentHash = contentHash(candidate)?.takeIf(String::isNotBlank) ?: return false
        if (expectedHash != null && currentHash != expectedHash) return false
        val candidateId = identity(candidate)
        return retainedCandidates.any { retained ->
            identity(retained) != candidateId && contentHash(retained) == currentHash
        }
    }
}
