package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.CleanResultModel
import com.aicleanerstorage.spaceclean.domain.model.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.MemoryCleanResultModel
import com.aicleanerstorage.spaceclean.domain.model.ScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.ScanResultModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Core clean capability contract.
 *
 * Domain callers depend on this abstraction rather than concrete scanner or
 * delete implementations.
 */
interface CleanRepository {
    val scanProgress: StateFlow<ScanProgressModel>
    val scanResult: StateFlow<ScanResultModel?>

    fun clearSession()
    fun removeCleanedFiles(files: List<JunkFileModel>)

    suspend fun performFullScan(): ScanResultModel

    suspend fun cleanMemory(): MemoryCleanResultModel

    suspend fun deleteFileItems(selectedFiles: List<JunkFileModel>): List<JunkDeleteOutcomeModel>

    suspend fun finalizeAuthorizedDeletes(pendingOutcomes: List<JunkDeleteOutcomeModel>): CleanResultModel
}
