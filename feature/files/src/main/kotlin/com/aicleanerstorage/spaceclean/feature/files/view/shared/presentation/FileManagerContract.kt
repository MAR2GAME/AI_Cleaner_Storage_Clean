package com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateFileEntry
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateGroupItem

interface FileManagerIntent {
    data class Navigate(val destination: AppDestination, val reset: Boolean = true) : FileManagerIntent
    data object PermissionRejected : FileManagerIntent
    data object CloseDetailAndBack : FileManagerIntent

    data object Back : FileManagerIntent
    data object CancelDelete : FileManagerIntent
    data object RequestDelete : FileManagerIntent
    data object DeleteReady : FileManagerIntent
    data object ContinueManaging : FileManagerIntent
    data object ToggleVisibleItems : FileManagerIntent
    data class SelectTab(val index: Int) : FileManagerIntent
    data class ToggleSelection(val id: Int) : FileManagerIntent
    data class ToggleVisibleIds(val ids: Set<Int>) : FileManagerIntent
    data class ToggleGroupIds(val ids: Set<Int>) : FileManagerIntent
    data class OpenDetail(val index: Int) : FileManagerIntent
    data class ShowStopDialog(val blockedPhase: FileOperationPhase? = null) : FileManagerIntent
    data object DismissStopDialog : FileManagerIntent
    data object CancelActiveOperation : FileManagerIntent
    data object CancelDeletingAndReturnToBrowsing : FileManagerIntent
    data object CompletionAdFinished : FileManagerIntent
    data object StartIfNeeded : FileManagerIntent
    data object Refresh : FileManagerIntent
    data object CloseDetail : FileManagerIntent
    data object RejectSystemDelete : FileManagerIntent
    data object ClearError : FileManagerIntent
    data object DeleteSelectedFiles : FileManagerIntent
    data class OpenGroup(val group: DuplicateGroupItem) : FileManagerIntent
    data object CloseGroup : FileManagerIntent
    data object ToggleAll : FileManagerIntent
    data object AcceptWarning : FileManagerIntent
    data class ToggleFile(val file: DuplicateFileEntry) : FileManagerIntent
    data object AutoSelectCurrentGroup : FileManagerIntent
    data class ToggleGroupSelection(val group: DuplicateGroupItem) : FileManagerIntent
    data object ToggleCurrentGroupSelection : FileManagerIntent
    data object RequestDeleteCurrentGroup : FileManagerIntent
    data object ClearSkippedDeleteCount : FileManagerIntent
    data object RequestRemoveLocation : FileManagerIntent
    data object CancelRemoveLocation : FileManagerIntent
    data object RemoveLocationData : FileManagerIntent
    data class ToggleGroup(val ids: Set<Int>) : FileManagerIntent

}

sealed interface FileManagerEffect {
    data class Operation(val event: FeatureOperationEvent) : FileManagerEffect
    data class Exit(val reason: FeatureExitReason, val home: Boolean) : FileManagerEffect
    data class Navigate(val destination: AppDestination, val reset: Boolean) : FileManagerEffect
    data object Back : FileManagerEffect
}
