package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileGroupModel
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/**
 * 文件列表与删除的业务契约，按页面用途查询，不暴露 MediaStore / SAF。
 *
 * 查询方法正常返回；删除是写操作，failure 表示整批没有执行，
 * 成功值里的计数描述部分失败。调用方不要捕获平台异常。
 */
interface ManagedFileRepository {
    /**
     * 查询图片。
     *
     * @return 图片列表；无存储权限或没有图片时为空列表。
     */
    suspend fun loadImages(): List<ManagedFileModel>

    /**
     * 查询视频。
     *
     * @return 视频列表；无存储权限或没有视频时为空列表。
     */
    suspend fun loadVideos(): List<ManagedFileModel>

    /**
     * 查询音频。
     *
     * @return 音频列表；无存储权限或没有音频时为空列表。
     */
    suspend fun loadAudios(): List<ManagedFileModel>

    /**
     * 查询截图。
     *
     * @return 截图列表；无存储权限或没有截图时为空列表。
     */
    suspend fun loadScreenshots(): List<ManagedFileModel>

    /**
     * 查询带位置信息的隐私图片。
     *
     * @return 图片列表；位置权限缺失时由页面先处理授权。
     */
    suspend fun loadPrivacyImages(): List<ManagedFileModel>

    /**
     * 查询文档。
     *
     * @return 文档列表；无存储权限或没有文档时为空列表。
     */
    suspend fun loadDocuments(): List<ManagedFileModel>

    /**
     * 查询大文件。
     *
     * @param minBytes 最小文件大小，默认 [DEFAULT_LARGE_FILE_BYTES]。
     * @return 满足阈值的大文件列表。
     */
    suspend fun loadLargeFiles(minBytes: Long = DEFAULT_LARGE_FILE_BYTES): List<ManagedFileModel>

    /**
     * 查询重复文件分组。
     *
     * @return 按内容哈希分组的文件；没有重复时为空列表。
     */
    suspend fun loadDuplicateGroups(): List<DuplicateFileGroupModel>

    /**
     * 删除选中文件。
     *
     * @param files 待删除文件；空列表直接返回成功。
     * @return 删除汇总；failure 表示整批未开始或平台删除失败。
     */
    suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel>

    /**
     * 删除重复文件选择。
     *
     * @param selections 文件与所属分组的配对；每组至少保留一个文件。
     * @return 删除汇总；failure 表示整批未开始或安全校验失败。
     */
    suspend fun deleteDuplicates(selections: List<DuplicateFileSelectionModel>): Result<FileDeleteSummaryModel>

    /**
     * 移除图片的位置元数据。
     *
     * @param files 待处理图片；空列表直接返回成功。
     * @return 成功移除的图片数量；failure 表示平台写入失败。
     */
    suspend fun removeLocationData(files: List<ManagedFileModel>): Result<Int>

    companion object {
        const val DEFAULT_LARGE_FILE_BYTES = 10L * 1024 * 1024
    }
}
