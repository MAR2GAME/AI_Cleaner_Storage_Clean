package com.aicleanerstorage.spaceclean.feature.junkclean.domain

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanupSummary
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanSessionState

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.MemoryCleanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanProgress
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanResult
import kotlinx.coroutines.flow.StateFlow

interface CleanSessionStore {
    val scanProgress: StateFlow<ScanProgress>

    val scanResult: StateFlow<ScanResult?>

    val sessionState: StateFlow<ScanSessionState>

    val cleanupSummary: StateFlow<CleanupSummary?>

    val cleanResult: StateFlow<CleanResult?>

    val memoryResult: StateFlow<MemoryCleanResult?>

    fun setScanProgress(value: ScanProgress)

    fun setScanResult(value: ScanResult)

    fun setCleanResult(value: CleanResult)

    fun setMemoryResult(value: MemoryCleanResult)

    fun setCleanupSummary(value: CleanupSummary)

    fun removeCleanedFiles(cleanedFiles: List<JunkFile>)

    fun clearCleanResults()

    fun clear()
}
