package com.aicleanerstorage.spaceclean.domain.similarphotos.port

import com.aicleanerstorage.spaceclean.domain.files.model.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.files.model.ManagedFileModel

/** Similar-photo capability: scan groups of look-alike images and delete selected ones. */
interface SimilarPhotoRepository {
    suspend fun loadGroups(): List<List<ManagedFileModel>>

    suspend fun delete(files: List<ManagedFileModel>): FileDeleteSummaryModel
}
