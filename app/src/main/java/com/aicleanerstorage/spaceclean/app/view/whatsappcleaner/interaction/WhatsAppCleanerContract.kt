package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel

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
    val files: List<ManagedFileItemModel>,
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

sealed interface WhatsAppCleanerAction {
    data class NavigationRequested(val destination: AppDestination) : WhatsAppCleanerAction
    data object BackClicked : WhatsAppCleanerAction
    data object CompletionExitClicked : WhatsAppCleanerAction
    data object ScanStartRequested : WhatsAppCleanerAction
    data object CleanClicked : WhatsAppCleanerAction
    data object RetryClicked : WhatsAppCleanerAction
    data object OperationCancellationRequested : WhatsAppCleanerAction
    data object CleaningCancellationRequested : WhatsAppCleanerAction
    data object ResultDismissed : WhatsAppCleanerAction
    data object ResultRequested : WhatsAppCleanerAction
    data class GroupToggled(val group: WhatsAppCleanerGroup) : WhatsAppCleanerAction
    data class CategoryToggled(
        val group: WhatsAppCleanerGroup,
        val category: WhatsAppCleanerCategory,
    ) : WhatsAppCleanerAction
    data class GroupExpansionToggled(val group: WhatsAppCleanerGroup) : WhatsAppCleanerAction

    data object InitialScanRequested : WhatsAppCleanerAction
    data object SelectedFilesCleanRequested : WhatsAppCleanerAction
}


sealed interface WhatsAppCleanerEvent {

    data class Exit(val home: Boolean) : WhatsAppCleanerEvent
    data class Navigate(val destination: AppDestination) : WhatsAppCleanerEvent
}
