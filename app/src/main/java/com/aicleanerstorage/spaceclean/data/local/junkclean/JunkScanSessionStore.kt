package com.aicleanerstorage.spaceclean.data.local.junkclean

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanResultModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 垃圾扫描会话的进程内状态。
 *
 * 只保存跨页需要的事实（进度、结果），不保存页面展示状态；
 * 由 `DefaultJunkCleanRepository` 持有，页面通过仓库端口观察。
 */
internal class JunkScanSessionStore {
    private val _scanProgress = MutableStateFlow(JunkScanProgressModel.IDLE)
    private val _scanResult = MutableStateFlow<JunkScanResultModel?>(null)

    /** 当前扫描进度；空闲时为 [JunkScanProgressModel.IDLE]。 */
    val scanProgress: StateFlow<JunkScanProgressModel> = _scanProgress.asStateFlow()

    /** 最近一次完整扫描结果；没有结果时为 null。 */
    val scanResult: StateFlow<JunkScanResultModel?> = _scanResult.asStateFlow()

    /** 更新扫描进度；只由扫描流程调用。 */
    fun setScanProgress(value: JunkScanProgressModel) {
        _scanProgress.value = value
    }

    /** 保存最近一次完整扫描结果；只由扫描流程调用。 */
    fun setScanResult(value: JunkScanResultModel) {
        _scanResult.value = value
    }

    /**
     * 删除已确认清理的文件，并同步剩余数量和可释放空间。
     *
     * @param cleanedFiles 已成功删除或已不存在的文件。
     */
    fun removeCleanedFiles(cleanedFiles: List<JunkFileModel>) {
        val current = _scanResult.value ?: return
        if (cleanedFiles.isEmpty()) return

        val removed = cleanedFiles.toHashSet()
        val remainingFiles = current.files.filterNot { it in removed }
        val updatedResult = current.copy(files = remainingFiles)

        _scanResult.value = updatedResult
        _scanProgress.value =
            if (remainingFiles.isEmpty()) {
                JunkScanProgressModel.IDLE
            } else {
                JunkScanProgressModel(
                    percent = 100f,
                    foundCount = remainingFiles.size,
                    foundSize = updatedResult.totalSize,
                )
            }
    }

    /** 清空进度和结果；开始新扫描或离开流程时调用。 */
    @Synchronized
    fun clear() {
        _scanProgress.value = JunkScanProgressModel.IDLE
        _scanResult.value = null
    }
}
