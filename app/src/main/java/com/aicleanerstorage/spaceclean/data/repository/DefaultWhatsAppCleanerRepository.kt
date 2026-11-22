package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.data.local.whatsapp.WhatsAppFileDataSource
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppFileBucketModel
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
import com.aicleanerstorage.spaceclean.domain.port.WhatsAppCleanerRepository

/**
 * WhatsApp 清理仓库：目录查询交给 [WhatsAppFileDataSource]，删除复用文件端口。
 *
 * 不再依赖具体文件仓库实现，data 内部保持 repository → data source / port 的单向依赖。
 *
 * @param source WhatsApp 目录数据源。
 * @param files 文件删除与查询端口。
 */
internal class DefaultWhatsAppCleanerRepository(
    private val source: WhatsAppFileDataSource,
    private val files: ManagedFileRepository,
) : WhatsAppCleanerRepository {
    override suspend fun loadGroups(): Map<WhatsAppFileBucketModel, List<ManagedFileModel>> = source.loadGroups()

    override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> =
        this.files.delete(files)
}
