package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppFileBucketModel

/** WhatsApp cleanup capability: scan buckets and delete selected files. */
interface WhatsAppCleanerRepository {
    suspend fun loadGroups(): Map<WhatsAppFileBucketModel, List<ManagedFileModel>>

    suspend fun delete(files: List<ManagedFileModel>): FileDeleteSummaryModel
}
