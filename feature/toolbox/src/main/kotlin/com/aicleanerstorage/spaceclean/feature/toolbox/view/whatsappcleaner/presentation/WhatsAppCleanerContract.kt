package com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem

enum class WhatsAppCleanerPhase {
    Scanning,
    ScanResult,
    Cleaning,
    CompleteAnimation,
    Result,
    Error,
}

enum class WhatsAppCleanerGroup {
    Cache,
    File,
}

enum class WhatsAppCleanerCategory {
    Images,
    Videos,
    Audios,
    Documents,
    Databases,
    Other,
}

data class WhatsAppCleanerSubItem(
    val group: WhatsAppCleanerGroup,
    val category: WhatsAppCleanerCategory,
    val files: List<ManagedFileItem>,
    val selected: Boolean,
) {
    val totalBytes: Long = files.sumOf { it.sizeBytes }
    val hasFiles: Boolean = totalBytes > 0L
}

data class WhatsAppCleanerGroupItem(
    val group: WhatsAppCleanerGroup,
    val children: List<WhatsAppCleanerSubItem>,
    val expanded: Boolean = true,
) {
    val totalBytes: Long = children.sumOf { it.totalBytes }
    val selectedBytes: Long = children.filter { it.selected }.sumOf { it.totalBytes }
    val hasFiles: Boolean = children.any { it.hasFiles }
    val selected: Boolean = hasFiles && children.filter { it.hasFiles }.all { it.selected }
}

data class WhatsAppCleanerUiState(
    val phase: WhatsAppCleanerPhase = WhatsAppCleanerPhase.Scanning,
    val groups: List<WhatsAppCleanerGroupItem> = emptyList(),
    val scannedBytes: Long = 0L,
    val selectedBytes: Long = 0L,
    val selectedCount: Int = 0,
    val deletedBytes: Long = 0L,
    val deletedCount: Int = 0,
    val errorMessage: String? = null,
)

sealed interface WhatsAppCleanerIntent {
    data class Navigate(val destination: AppDestination) : WhatsAppCleanerIntent
    data object Back : WhatsAppCleanerIntent
    data object ExitAfterComplete : WhatsAppCleanerIntent
    data object StartScan : WhatsAppCleanerIntent
    data object CleanSelected : WhatsAppCleanerIntent
    data object Retry : WhatsAppCleanerIntent
    data object CancelActiveOperation : WhatsAppCleanerIntent
    data object CancelCleaningAndReturnToResult : WhatsAppCleanerIntent
    data object ClearResult : WhatsAppCleanerIntent
    data object ShowResult : WhatsAppCleanerIntent
    data class ToggleGroup(val group: WhatsAppCleanerGroup) : WhatsAppCleanerIntent
    data class ToggleCategory(
        val group: WhatsAppCleanerGroup,
        val category: WhatsAppCleanerCategory,
    ) : WhatsAppCleanerIntent
    data class ToggleExpanded(val group: WhatsAppCleanerGroup) : WhatsAppCleanerIntent

    data object StartScanIfNeeded : WhatsAppCleanerIntent
    data object CleanSelectedFiles : WhatsAppCleanerIntent
}

internal data class WhatsAppCleanerStateChanged(val value: WhatsAppCleanerUiState) : WhatsAppCleanerIntent

sealed interface WhatsAppCleanerEffect {
    data class Operation(val event: FeatureOperationEvent) : WhatsAppCleanerEffect
    data class Exit(val home: Boolean) : WhatsAppCleanerEffect
    data class Navigate(val destination: AppDestination) : WhatsAppCleanerEffect
}

