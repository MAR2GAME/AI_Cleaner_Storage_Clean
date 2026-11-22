package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppFileBucketModel

/**
 * WhatsApp 清理能力：扫描目录分桶并删除选中文件。
 *
 * 删除是写操作，failure 表示整批未执行或平台删除失败。
 */
interface WhatsAppCleanerRepository {
    /**
     * 扫描 WhatsApp 文件分桶。
     *
     * @return 分桶到文件列表的映射；没有文件时为空映射。
     */
    suspend fun loadGroups(): Map<WhatsAppFileBucketModel, List<ManagedFileModel>>

    /**
     * 删除选中的 WhatsApp 文件。
     *
     * @param files 待删除文件；空列表直接返回成功。
     * @return 删除汇总；failure 表示整批未开始或删除失败。
     */
    suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel>
}
