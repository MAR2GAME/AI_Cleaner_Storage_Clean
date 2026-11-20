package com.aicleanerstorage.cleanerkit.clean

import com.aicleanerstorage.cleanerkit.clean.model.JunkDeleteOutcome
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.clean.model.MemoryCleanResult
import com.aicleanerstorage.cleanerkit.clean.model.ScanProgress
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult
import kotlinx.coroutines.flow.StateFlow

/**
 * Core clean capability contract.
 *
 * Domain callers depend on this abstraction rather than concrete scanner or
 * delete implementations.
 */
interface CleanRepository {
    val scanProgress: StateFlow<ScanProgress>
    val scanResult: StateFlow<ScanResult?>

    fun clearSession()
    fun removeCleanedFiles(files: List<JunkFile>)

    suspend fun performFullScan(): ScanResult

    suspend fun cleanMemory(): MemoryCleanResult

    suspend fun deleteFileItems(selectedFiles: List<JunkFile>): List<JunkDeleteOutcome>

    suspend fun deleteFileItems(
        selectedFiles: List<JunkFile>,
        shouldStop: () -> Boolean,
    ): List<JunkDeleteOutcome> = if (shouldStop()) emptyList() else deleteFileItems(selectedFiles)

}
