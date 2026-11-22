package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel

/**
 * 垃圾扫描结果的整理规则。
 *
 * 重复文件优先展示，同一路径只保留第一条；具体扫描由 data 层完成，
 * 规则本身不读文件系统。
 */
internal object JunkScanRule {
    /**
     * 去重并排序扫描结果。
     *
     * @param files 各扫描器产出的原始文件。
     * @return 重复文件在前、同一路径唯一的列表。
     */
    fun normalize(files: List<JunkFileModel>): List<JunkFileModel> =
        files
            .sortedBy { if (it.category == JunkCategory.DUPLICATE) 0 else 1 }
            .distinctBy { it.path }
}
