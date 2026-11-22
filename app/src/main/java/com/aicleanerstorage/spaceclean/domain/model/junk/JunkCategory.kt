package com.aicleanerstorage.spaceclean.domain.model.junk

/** 垃圾清理的分类；页面按此分组，kit 扫描器只负责产出对应枚举。 */
enum class JunkCategory {
    CACHE,
    TEMP_FILE,
    RESIDUAL,
    APK,
    DUPLICATE,
    LARGE_FILE,
}
