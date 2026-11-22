package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkMemoryCleanModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanResultModel
import kotlinx.coroutines.flow.StateFlow

/**
 * 垃圾扫描与清理的业务契约。
 *
 * [scanProgress] 在扫描期间持续更新；[scanResult] 保存最近一次完整扫描。
 * `scan` / `deleteFiles` 的 failure 表示整批没有开始或基础设施异常，
 * 逐文件结果在成功值里。
 */
interface JunkCleanRepository {
    /** 当前扫描进度；空闲时为初始进度。 */
    val scanProgress: StateFlow<JunkScanProgressModel>

    /** 最近一次完整扫描结果；尚未扫描时为 null。 */
    val scanResult: StateFlow<JunkScanResultModel?>

    /** 清空扫描会话与缓存结果，不删除文件。 */
    fun clearSession()

    /**
     * 从扫描会话中移除已经清理的文件。
     *
     * @param files 已成功删除的文件。
     */
    fun removeCleanedFiles(files: List<JunkFileModel>)

    /**
     * 执行一次垃圾扫描。
     *
     * @return 扫描结果；failure 表示扫描未能开始或扫描会话异常。
     */
    suspend fun scan(): Result<JunkScanResultModel>

    /**
     * 执行内存清理。
     *
     * @return 清理结果；failure 表示内存清理不可用或执行失败。
     */
    suspend fun cleanMemory(): Result<JunkMemoryCleanModel>

    /**
     * 删除选中的垃圾文件。
     *
     * @param files 待删除文件；空列表直接返回成功。
     * @param shouldStop 返回 true 时停止继续删除，已删除结果仍会返回。
     * @return 逐文件结果；failure 表示整批未开始或基础设施异常。
     */
    suspend fun deleteFiles(
        files: List<JunkFileModel>,
        shouldStop: () -> Boolean = { false },
    ): Result<List<JunkDeleteOutcomeModel>>
}
