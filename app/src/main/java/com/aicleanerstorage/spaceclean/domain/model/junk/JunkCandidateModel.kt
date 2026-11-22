package com.aicleanerstorage.spaceclean.domain.model.junk

/**
 * 扫描器枚举出的候选文件，只携带分类所需的纯数据。
 *
 * `modifiedMillis` 由平台在枚举时读取，分类规则只使用传入的时间快照，
 * 不自行读取当前时间，便于纯 JVM 测试。
 */
data class JunkCandidateModel(
    val path: String,
    val name: String,
    val sizeBytes: Long,
    val modifiedMillis: Long,
    val source: JunkSource = JunkSource.PublicStorage,
)
