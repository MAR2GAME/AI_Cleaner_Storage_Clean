package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileGroupModel
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/**
 * 文件列表与删除的业务契约，按页面用途查询，不暴露 MediaStore / SAF。
 *
 * 查询方法正常返回；删除是写操作，failure 表示整批没有执行，
 * 成功值里的计数描述部分失败。调用方不捕获平台异常。
 */
interface ManagedFileRepository {
    suspend fun loadImages(): List<ManagedFileModel>

    suspend fun loadVideos(): List<ManagedFileModel>

    suspend fun loadAudios(): List<ManagedFileModel>

    suspend fun loadScreenshots(): List<ManagedFileModel>

    suspend fun loadPrivacyImages(): List<ManagedFileModel>

    suspend fun loadDocuments(): List<ManagedFileModel>

    suspend fun loadLargeFiles(minBytes: Long = DEFAULT_LARGE_FILE_BYTES): List<ManagedFileModel>

    suspend fun loadDuplicateGroups(): List<DuplicateFileGroupModel>

    suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel>

    suspend fun deleteDuplicates(
        selections: List<DuplicateFileSelectionModel>,
    ): Result<FileDeleteSummaryModel>

    suspend fun removeLocationData(files: List<ManagedFileModel>): Result<Int>

    companion object {
        const val DEFAULT_LARGE_FILE_BYTES = 10L * 1024 * 1024
    }
}
