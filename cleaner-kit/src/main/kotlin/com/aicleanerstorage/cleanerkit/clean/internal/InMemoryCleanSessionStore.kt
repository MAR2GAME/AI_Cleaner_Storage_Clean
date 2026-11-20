package com.aicleanerstorage.cleanerkit.clean.internal

import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.clean.model.ScanProgress
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult
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
    private val _scanProgress = MutableStateFlow(ScanProgress.IDLE)
    private val _scanResult = MutableStateFlow<ScanResult?>(null)

    val scanProgress: StateFlow<ScanProgress> = _scanProgress.asStateFlow()

    val scanResult: StateFlow<ScanResult?> = _scanResult.asStateFlow()

    fun setScanProgress(value: ScanProgress) {
        _scanProgress.value = value
    }

    fun setScanResult(value: ScanResult) {
        _scanResult.value = value
    }

    fun removeCleanedFiles(cleanedFiles: List<JunkFile>) {
        val current = _scanResult.value ?: return
        if (cleanedFiles.isEmpty()) return

        val removed = cleanedFiles.toHashSet()
        val remainingFiles = current.junkFiles.filterNot { it in removed }
        val updatedResult = current.copy(junkFiles = remainingFiles)

        _scanResult.value = updatedResult
        _scanProgress.value = if (remainingFiles.isEmpty()) {
            ScanProgress.IDLE
        } else {
            ScanProgress(percent = 100f, foundCount = remainingFiles.size, foundSize = updatedResult.totalSize)
        }
    }

    @Synchronized
    fun clear() {
        _scanProgress.value = ScanProgress.IDLE
        _scanResult.value = null
    }
}
