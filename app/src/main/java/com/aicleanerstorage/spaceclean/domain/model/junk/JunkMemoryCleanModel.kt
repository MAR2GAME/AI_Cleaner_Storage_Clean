package com.aicleanerstorage.spaceclean.domain.model.junk

/** 内存清理结果；只有 [freedBytes] 参与完成页展示。 */
data class JunkMemoryCleanModel(
    val freedBytes: Long,
)
