package com.aicleanerstorage.spaceclean.app.view.duplicates.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/** 重复文件页的完整动作集合。 */
sealed interface DuplicateFilesAction {
    data class NavigationRequested(
        val destination: AppDestination,
        val reset: Boolean = true,
    ) : DuplicateFilesAction

    data object PermissionRejected : DuplicateFilesAction

    data object BackClicked : DuplicateFilesAction

    data object StopDismissed : DuplicateFilesAction

    data object OperationCancellationRequested : DuplicateFilesAction

    data object InitialLoadRequested : DuplicateFilesAction

    data object RefreshRequested : DuplicateFilesAction

    data class GroupClicked(
        val group: DuplicateGroupItem,
    ) : DuplicateFilesAction

    data object SelectAllClicked : DuplicateFilesAction

    data object WarningConfirmed : DuplicateFilesAction

    data class FileToggled(
        val file: DuplicateFileEntry,
    ) : DuplicateFilesAction

    data object AutoSelectClicked : DuplicateFilesAction

    data class GroupSelectionToggled(
        val group: DuplicateGroupItem,
    ) : DuplicateFilesAction

    data object DeleteClicked : DuplicateFilesAction

    data object GroupDeleteClicked : DuplicateFilesAction

    data object DeleteDismissed : DuplicateFilesAction

    data object ErrorDismissed : DuplicateFilesAction

    data object SkippedDeleteCountConsumed : DuplicateFilesAction

    data object DeleteConfirmed : DuplicateFilesAction

    data object ContinueManagingClicked : DuplicateFilesAction
}
