package com.aicleanerstorage.spaceclean.data.similarphotos.internal

import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.spaceclean.data.files.toItem
import com.aicleanerstorage.spaceclean.data.files.toModel
import com.aicleanerstorage.spaceclean.data.similarphotos.groupSimilarPhotos
import com.aicleanerstorage.spaceclean.domain.files.model.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.files.model.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.similarphotos.port.SimilarPhotoRepository

internal class SimilarPhotoRepositoryImpl(
    private val files: FileRepository,
) : SimilarPhotoRepository {
    override suspend fun loadGroups(): List<List<ManagedFileModel>> =
        groupSimilarPhotos(files.loadImages()).map { group -> group.map { it.toModel() } }

    override suspend fun delete(files: List<ManagedFileModel>): FileDeleteSummaryModel {
        val result = this.files.deleteFiles(files.map { it.toItem() })
        return FileDeleteSummaryModel(result.deletedCount, result.freedBytes)
    }
}
