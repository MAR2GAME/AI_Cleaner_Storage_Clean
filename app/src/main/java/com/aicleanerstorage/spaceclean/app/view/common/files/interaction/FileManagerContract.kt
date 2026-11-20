package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFileEntry
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateGroupItem

interface FileManagerAction {
    data class NavigationRequested(val destination: AppDestination, val reset: Boolean = true) : FileManagerAction
    data object PermissionRejected : FileManagerAction
    data object DetailBackClicked : FileManagerAction

    data object BackClicked : FileManagerAction
    data object DeleteDismissed : FileManagerAction
    data object DeleteClicked : FileManagerAction
    data object DeletePrepared : FileManagerAction
    data object ContinueManagingClicked : FileManagerAction
    data object VisibleItemsToggled : FileManagerAction
    data class TabChangeRequested(val index: Int) : FileManagerAction
    data class SelectionToggled(val id: Int) : FileManagerAction
    data class VisibleSelectionToggled(val ids: Set<Int>) : FileManagerAction
    data class GroupItemsToggled(val ids: Set<Int>) : FileManagerAction
    data class DetailClicked(val index: Int) : FileManagerAction
    data class StopClicked(val blockedPhase: FileOperationPhase? = null) : FileManagerAction
    data object StopDismissed : FileManagerAction
    data object OperationCancellationRequested : FileManagerAction
    data object DeleteCancellationRequested : FileManagerAction
    data object InitialLoadRequested : FileManagerAction
    data object RefreshRequested : FileManagerAction
    data object DetailDismissed : FileManagerAction
    data object SystemDeleteRejected : FileManagerAction
    data object ErrorDismissed : FileManagerAction
    data object DeleteConfirmed : FileManagerAction
    data class GroupClicked(val group: DuplicateGroupItem) : FileManagerAction
    data object GroupDismissed : FileManagerAction
    data object SelectAllClicked : FileManagerAction
    data object WarningConfirmed : FileManagerAction
    data class FileToggled(val file: DuplicateFileEntry) : FileManagerAction
    data object AutoSelectClicked : FileManagerAction
    data class GroupSelectionToggled(val group: DuplicateGroupItem) : FileManagerAction
    data object CurrentGroupSelectionToggled : FileManagerAction
    data object GroupDeleteClicked : FileManagerAction
    data object SkippedDeleteCountConsumed : FileManagerAction
    data object LocationRemovalClicked : FileManagerAction
    data object LocationRemovalDismissed : FileManagerAction
    data object LocationRemovalConfirmed : FileManagerAction
    data class GroupToggled(val ids: Set<Int>) : FileManagerAction

}

sealed interface FileManagerEvent {
    data class Exit(val home: Boolean) : FileManagerEvent
    data class Navigate(val destination: AppDestination, val reset: Boolean) : FileManagerEvent
    data object NavigateBack : FileManagerEvent
}
