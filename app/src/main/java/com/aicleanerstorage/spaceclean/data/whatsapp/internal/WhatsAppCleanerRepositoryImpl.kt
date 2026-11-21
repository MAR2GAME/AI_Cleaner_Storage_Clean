package com.aicleanerstorage.spaceclean.data.whatsapp.internal

import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.spaceclean.data.files.toItem
import com.aicleanerstorage.spaceclean.data.files.toModel
import com.aicleanerstorage.spaceclean.data.whatsapp.loadWhatsAppFileGroups
import com.aicleanerstorage.spaceclean.domain.files.model.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.files.model.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.whatsapp.model.WhatsAppFileBucket
import com.aicleanerstorage.spaceclean.domain.whatsapp.port.WhatsAppCleanerRepository

internal class WhatsAppCleanerRepositoryImpl(
    private val files: FileRepository,
) : WhatsAppCleanerRepository {
    override suspend fun loadGroups(): Map<WhatsAppFileBucket, List<ManagedFileModel>> =
        files.loadWhatsAppFileGroups().mapValues { (_, items) -> items.map { it.toModel() } }

    override suspend fun delete(files: List<ManagedFileModel>): FileDeleteSummaryModel {
        val result = this.files.deleteFiles(files.map { it.toItem() })
        return FileDeleteSummaryModel(result.deletedCount, result.freedBytes)
    }
}
