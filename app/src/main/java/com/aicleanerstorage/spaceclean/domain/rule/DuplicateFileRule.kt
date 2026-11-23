package com.aicleanerstorage.spaceclean.domain.rule

/**
 * 重复文件规则。
 *
 * 只做“同大小 + 同头部哈希 + 同内容哈希 + 至少两份”的判断；哈希与文件身份由
 * 调用方以函数参数传入，因此规则本身不读磁盘，也不依赖 Android。
 *
 * 头部哈希是第一道门槛：头部不同的文件内容必然不同，调用方就不必整文件读盘，
 * 这也是同尺寸文件很多时避免扫描卡顿的关键。
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

    /**
     * 可能重复的候选。
     *
     * 只保留非空、身份唯一且大小相同的候选；data 层用它决定预读哪些文件的头部，
     * 与 [group] 共用同一份筛选口径，避免两处判断漂移。
     *
     * @param candidates 待筛选文件。
     * @return 可能重复的候选；没有同尺寸组合时为空列表。
     */
    fun <T> possibleDuplicates(candidates: List<Candidate<T>>): List<Candidate<T>> =
        candidates
            .filter { it.sizeBytes > 0 }
            .distinctBy { it.identity }
            .groupBy { it.sizeBytes }
            .values
            .filter { it.size > 1 }
            .flatten()

    /**
     * 按大小、头部哈希和内容哈希分三段筛选。
     *
     * 头部哈希互不相同的文件可以确定内容不同，直接排除；只有头部相同的候选
     * 才做全量哈希，同尺寸但内容不同的文件因此不会被整文件读取。
     *
     * @param candidates 待分组文件。
     * @param prefixHash 文件头部哈希；返回 null 表示读不到，按未知处理。
     * @param contentHash 全量内容哈希；返回 null 表示读取失败或文件过大。
     * @return 重复文件组；每组按修改时间从新到旧排序，组间按可释放空间降序。
     */
    fun <T> group(
        candidates: List<Candidate<T>>,
        prefixHash: (T) -> String?,
        contentHash: (T) -> String?,
    ): List<Group<T>> =
        possibleDuplicates(candidates)
            .groupBy { it.sizeBytes }
            .values
            .flatMap { sameSize ->
                groupSameSize(
                    candidates = sameSize.sortedByDescending { it.modifiedMillis },
                    prefixHash = prefixHash,
                    contentHash = contentHash,
                )
            }.sortedByDescending { it.files.first().sizeBytes * (it.files.size - 1) }
            .map { group -> Group(group.hash, group.files.map { it.value }) }

    private fun <T> groupSameSize(
        candidates: List<Candidate<T>>,
        prefixHash: (T) -> String?,
        contentHash: (T) -> String?,
    ): List<Group<Candidate<T>>> {
        val prefixes = candidates.associateWith { prefixHash(it.value)?.takeIf(String::isNotBlank) }
        val fullHashCandidates =
            if (prefixes.containsValue(null)) {
                candidates
            } else {
                candidates
                    .groupBy(prefixes::get)
                    .values
                    .filter { it.size > 1 }
                    .flatten()
            }
        val hashes = fullHashCandidates.associateWith { contentHash(it.value)?.takeIf(String::isNotBlank) }
        return verifiedGroups(fullHashCandidates, hashes) + pendingGroups(fullHashCandidates, hashes)
    }

    private fun <T> verifiedGroups(
        candidates: List<Candidate<T>>,
        hashes: Map<Candidate<T>, String?>,
    ): List<Group<Candidate<T>>> =
        candidates
            .groupBy(hashes::get)
            .mapNotNull { (hash, files) -> if (hash != null && files.size > 1) Group(hash, files) else null }

    private fun <T> pendingGroups(
        candidates: List<Candidate<T>>,
        hashes: Map<Candidate<T>, String?>,
    ): List<Group<Candidate<T>>> =
        candidates
            .groupBy { it.name.lowercase() }
            .values
            .mapNotNull { names ->
                val unreadable = names.filter { hashes[it] == null }
                if (unreadable.isEmpty()) return@mapNotNull null
                val retained = names.firstOrNull { hashes[it] != null }
                val possible = listOfNotNull(retained) + unreadable
                if (possible.size > 1) Group(null, possible) else null
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
