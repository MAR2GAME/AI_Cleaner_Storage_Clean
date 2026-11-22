package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/** Similar-photo capability: scan groups of look-alike images and delete selected ones. */
interface SimilarPhotoRepository {
    suspend fun loadGroups(): List<List<ManagedFileModel>>

    suspend fun delete(files: List<ManagedFileModel>): FileDeleteSummaryModel
}
