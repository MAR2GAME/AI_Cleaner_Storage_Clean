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
 * [scan] / [deleteFiles] 的 failure 表示整批没有开始或基础设施异常，
 * 逐文件结果在成功值里。
 */
interface JunkCleanRepository {
    val scanProgress: StateFlow<JunkScanProgressModel>

    val scanResult: StateFlow<JunkScanResultModel?>

    fun clearSession()

    fun removeCleanedFiles(files: List<JunkFileModel>)

    suspend fun scan(): Result<JunkScanResultModel>

    suspend fun cleanMemory(): Result<JunkMemoryCleanModel>

    suspend fun deleteFiles(files: List<JunkFileModel>): Result<List<JunkDeleteOutcomeModel>>

    suspend fun deleteFiles(
        files: List<JunkFileModel>,
        shouldStop: () -> Boolean,
    ): Result<List<JunkDeleteOutcomeModel>>
}
