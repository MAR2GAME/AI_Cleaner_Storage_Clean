package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel

/**
 * 相似照片能力：扫描相似图片分组并删除选中项。
 *
 * 分组由领域规则产生；删除是写操作，failure 表示整批未执行或平台删除失败。
 */
interface SimilarPhotoRepository {
    /**
     * 扫描相似照片分组。
     *
     * @return 每个分组内的图片列表；没有相似照片时为空列表。
     */
    suspend fun loadGroups(): List<List<ManagedFileModel>>

    /**
     * 删除选中的相似照片。
     *
     * @param files 待删除图片；空列表直接返回成功。
     * @return 删除汇总；failure 表示整批未开始或删除失败。
     */
    suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel>
}
