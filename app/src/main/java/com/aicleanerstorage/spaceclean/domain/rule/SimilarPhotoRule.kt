package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/**
 * 相似照片分组规则。
 *
 * 按“相册 + 日期 + 大小桶”聚类，只在同一天、同一相册、大小接近的照片里
 * 找相似项；阈值和数量上限属于业务策略，放 domain，方便纯 JVM 测试。
 */
internal object SimilarPhotoRule {
    /**
     * 对照片分组。
     *
     * @param files 候选照片，按传入顺序截取前 [limit] 张。
     * @param limit 最多参与分组的照片数。
     * @return 相似照片组；每组按文件大小降序，最多返回 12 组。
     */
    fun group(
        files: List<ManagedFileModel>,
        limit: Int = DEFAULT_LIMIT,
    ): List<List<ManagedFileModel>> =
        files
            .take(limit)
            .groupBy { file ->
                PhotoBucket(
                    album = file.bucketName.orEmpty().lowercase(),
                    day = file.modifiedSeconds / SECONDS_PER_DAY,
                    sizeBucket = (file.sizeBytes / SIZE_BUCKET_BYTES).coerceAtLeast(0L),
                )
            }.values
            .filter { it.size > 1 }
            .take(MAX_GROUPS)
            .map { group -> group.sortedByDescending { it.sizeBytes } }

    private const val DEFAULT_LIMIT = 60
    private const val MAX_GROUPS = 12
    private const val SECONDS_PER_DAY = 86_400L
    private const val SIZE_BUCKET_BYTES = 512L * 1024L

    private data class PhotoBucket(
        val album: String,
        val day: Long,
        val sizeBucket: Long,
    )
}
