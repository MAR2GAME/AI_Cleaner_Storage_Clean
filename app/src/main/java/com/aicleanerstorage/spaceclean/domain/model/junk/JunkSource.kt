package com.aicleanerstorage.spaceclean.domain.model.junk

/**
 * 候选文件的来源类别。
 *
 * 只表达“来自应用缓存还是公共存储”，具体目录由 data 层决定；
 * 分类规则据此判断缓存文件，不接触平台路径。
 */
enum class JunkSource {
    PublicStorage,
    AppCache,
}
