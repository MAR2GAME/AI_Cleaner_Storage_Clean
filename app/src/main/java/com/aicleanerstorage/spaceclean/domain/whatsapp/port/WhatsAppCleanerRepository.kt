package com.aicleanerstorage.spaceclean.domain.whatsapp.port

import com.aicleanerstorage.spaceclean.domain.files.model.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.files.model.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.whatsapp.model.WhatsAppFileBucket

/** WhatsApp cleanup capability: scan buckets and delete selected files. */
interface WhatsAppCleanerRepository {
    suspend fun loadGroups(): Map<WhatsAppFileBucket, List<ManagedFileModel>>

    suspend fun delete(files: List<ManagedFileModel>): FileDeleteSummaryModel
}
