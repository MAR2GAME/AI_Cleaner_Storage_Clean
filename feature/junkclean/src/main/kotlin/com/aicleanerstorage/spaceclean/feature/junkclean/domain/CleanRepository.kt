package com.aicleanerstorage.spaceclean.feature.junkclean.domain

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkAuthorizedDeleteResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkDeleteOutcome

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanProgress
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.MemoryCleanResult
import kotlinx.coroutines.flow.Flow

/**
 * Core clean capability contract.
 *
 * Domain callers depend on this abstraction rather than concrete scanner or
 * delete implementations.
 */
interface CleanRepository {
    val scanProgress: Flow<ScanProgress>

    suspend fun performFullScan(): ScanResult

    suspend fun cleanFiles(selectedFiles: List<JunkFile>): CleanResult

    suspend fun cleanMemory(): MemoryCleanResult

    suspend fun deleteFileItems(selectedFiles: List<JunkFile>): List<JunkDeleteOutcome>

    suspend fun finalizeAuthorizedDeletes(pendingOutcomes: List<JunkDeleteOutcome>): JunkAuthorizedDeleteResult
}
