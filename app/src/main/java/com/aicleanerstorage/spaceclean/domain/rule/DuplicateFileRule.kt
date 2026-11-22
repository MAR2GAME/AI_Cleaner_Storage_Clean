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

    /**
     * 按大小和内容哈希分组。
     *
     * 只对大小相同且大于 0 的文件做哈希；哈希为空的文件不参与分组。
     *
     * @param candidates 待分组文件。
     * @param contentHash 内容哈希函数；返回 null 表示读取失败或文件过大。
     * @return 重复文件组；每组按修改时间从新到旧排序，组间按可释放空间降序。
     */
    fun <T> group(
        candidates: List<Candidate<T>>,
        contentHash: (T) -> String?,
    ): List<List<T>> {
        val uniqueFiles = candidates.filter { it.sizeBytes > 0L }.distinctBy { it.identity }
        val hashGroups = mutableListOf<List<Candidate<T>>>()

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
        if (expectedHash.isNullOrBlank()) return false
        val candidateId = identity(candidate)
        if (contentHash(candidate) != expectedHash) return false
        return retainedCandidates.any { retained ->
            identity(retained) != candidateId && contentHash(retained) == expectedHash
        }
    }
}
