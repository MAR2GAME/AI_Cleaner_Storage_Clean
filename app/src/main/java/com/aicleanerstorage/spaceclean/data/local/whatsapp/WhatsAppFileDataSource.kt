package com.aicleanerstorage.spaceclean.data.local.whatsapp

import com.aicleanerstorage.spaceclean.data.local.files.DirectoryFileDataSource
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppFileBucketModel
import com.aicleanerstorage.spaceclean.domain.rule.WhatsAppFileRule

/**
 * WhatsApp 文件数据源。
 *
 * 负责“扫哪些目录”，分类交给 [WhatsAppFileRule]；不依赖具体文件仓库实现。
 *
 * @param directories 指定目录的查询数据源。
 */
internal class WhatsAppFileDataSource(
    private val directories: DirectoryFileDataSource,
) {
    /**
     * 读取并分类 WhatsApp 文件。
     *
     * @return 按分类分组的文件；空目录返回空 Map。
     */
    suspend fun loadGroups(): Map<WhatsAppFileBucketModel, List<ManagedFileModel>> =
        directories
            .filesInDirectories(WHATSAPP_SCAN_DIRECTORIES, WhatsAppFileRule::isCandidate)
            .filter { it.sizeBytes > 0L }
            .groupBy(WhatsAppFileRule::classify)

    private companion object {
        val WHATSAPP_SCAN_DIRECTORIES =
            listOf(
                "WhatsApp",
                "WhatsApp Business",
                "Android/media/com.whatsapp/WhatsApp",
                "Android/media/com.whatsapp.w4b/WhatsApp Business",
                "Android/media/com.whatsapp.w4b/WhatsApp",
                "Android/media/com.gbwhatsapp/GBWhatsApp",
                "Android/media/com.yowhatsapp/YoWhatsApp",
                "Android/media/com.fmwhatsapp/FMWhatsApp",
            )
    }
}
