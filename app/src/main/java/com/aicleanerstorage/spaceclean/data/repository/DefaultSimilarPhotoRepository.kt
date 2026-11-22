package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
import com.aicleanerstorage.spaceclean.domain.port.SimilarPhotoRepository
import com.aicleanerstorage.spaceclean.domain.rule.SimilarPhotoRule

/**
 * 相似照片查询：取图片列表后交给 [SimilarPhotoRule] 分组，删除复用文件端口。
 *
 * 依赖 [ManagedFileRepository] 端口而不是具体实现，避免 data 内部形成
 * “repository 依赖 repository 实现”的反向耦合。
 *
 * @param repository 文件查询与删除端口。
 */
internal class DefaultSimilarPhotoRepository(
    private val repository: ManagedFileRepository,
) : SimilarPhotoRepository {
    override suspend fun loadGroups(): List<List<ManagedFileModel>> = SimilarPhotoRule.group(repository.loadImages())

    override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> = repository.delete(files)
}
