package com.aicleanerstorage.spaceclean.data.local

import com.aicleanerstorage.spaceclean.domain.model.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.ScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.ScanResultModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared state for the main clean flow.
 *
 * This object stores scan and clean facts that must survive cross-screen flows.
 * UI display state, animation state, and navigation remain outside this module.
 */
internal class InMemoryCleanSessionStore {
    private val _scanProgress = MutableStateFlow(ScanProgressModel.IDLE)
    private val _scanResult = MutableStateFlow<ScanResultModel?>(null)

    val scanProgress: StateFlow<ScanProgressModel> = _scanProgress.asStateFlow()

    val scanResult: StateFlow<ScanResultModel?> = _scanResult.asStateFlow()

    fun setScanProgress(value: ScanProgressModel) {
        _scanProgress.value = value
    }

    fun setScanResult(value: ScanResultModel) {
        _scanResult.value = value
    }

    fun removeCleanedFiles(cleanedFiles: List<JunkFileModel>) {
        val current = _scanResult.value ?: return
        if (cleanedFiles.isEmpty()) return

        val cleanedPaths = cleanedFiles.map { it.filePath }.toSet()
        val remainingFiles = current.junkFiles.filterNot { it.filePath in cleanedPaths }
        val remainingSize = remainingFiles.sumOf { it.fileSize }
        val updatedResult =
            current.copy(
                junkFiles = remainingFiles,
                totalSize = remainingSize,
                totalCount = remainingFiles.size,
                categoryBreakdown = remainingFiles.groupBy { it.category },
            )

        _scanResult.value = updatedResult
        _scanProgress.value = if (remainingFiles.isEmpty()) {
            ScanProgressModel.IDLE
        } else {
            ScanProgressModel(percent = 100f, foundCount = remainingFiles.size, foundSize = remainingSize)
        }
    }

    fun clear() {
        _scanProgress.value = ScanProgressModel.IDLE
        _scanResult.value = null
    }
}
