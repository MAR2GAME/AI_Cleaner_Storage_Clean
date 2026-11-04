package com.aicleanerstorage.spaceclean.feature.junkclean.presentation

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanResult

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.model.CategoryCleanGroup
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanupSummary
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent

sealed interface JunkCleanPhase {
    data object Idle : JunkCleanPhase
    data class CheckingPermission(val request: JunkPermissionRequest) : JunkCleanPhase
    data object Scanning : JunkCleanPhase
    data object Preview : JunkCleanPhase
    data object Cleaning : JunkCleanPhase
    data object AwaitingAuthorization : JunkCleanPhase
    data object CompleteAnimation : JunkCleanPhase
    data object Complete : JunkCleanPhase
    data class Failed(val operation: JunkOperation) : JunkCleanPhase
}

enum class JunkOperation {
    Scan,
    Clean,
}

data class JunkCleanResultUiState(
    val freedSpace: Long = 0L,
    val cleanedCount: Int = 0,
    val failedCount: Int = 0,
    val memoryFreedBytes: Long = 0L,
    val memoryProcessesKilled: Int = 0,
    val totalFreedBytes: Long = 0L,
    val formattedFreedSpace: String = "",
    val hasVisibleResult: Boolean = false,
)

data class SelectionSummary(
    val checkedCount: Int = 0,
    val checkedSize: Long = 0L,
    val checkedEmptyCategoryCount: Int = 0,
)

data class JunkCleanUiState(
    val phase: JunkCleanPhase = JunkCleanPhase.Idle,
    val progress: Float = 0f,
    val currentCategory: JunkCategory? = null,
    val foundItemCount: Int = 0,
    val foundTotalSize: Long = 0L,
    val formattedFoundSize: String = "0 B",
    val groups: List<CategoryCleanGroup> = emptyList(),
    val checkedEmptyCategories: Set<JunkCategory> = emptySet(),
    val selectedSummary: SelectionSummary = SelectionSummary(),
    val cleanResult: JunkCleanResultUiState = JunkCleanResultUiState(),
    val pendingAuthorizationCount: Int = 0,
    @StringRes val errorMessageRes: Int? = null,
    val errorMessage: String? = null,
    val showStopDialog: Boolean = false,
    val stopDialogPhase: JunkCleanPhase? = null,
)

enum class JunkPermissionRequest {
    Scan,
    Clean,
}

sealed interface JunkCleanAction {
    data class PermissionCheckStarted(val request: JunkPermissionRequest) : JunkCleanAction
    data class PermissionCheckRestored(
        val request: JunkPermissionRequest,
        val previous: JunkCleanUiState,
    ) : JunkCleanAction
    data class PermissionDenied(val request: JunkPermissionRequest) : JunkCleanAction
    data class PermissionBusy(val request: JunkPermissionRequest) : JunkCleanAction
    data object ScanStarted : JunkCleanAction

    data class ScanProgressUpdated(
        val progress: Float,
        val currentCategory: JunkCategory?,
        val foundItemCount: Int,
        val foundTotalSize: Long,
    ) : JunkCleanAction

    data class ScanCompleted(val result: ScanResult) : JunkCleanAction
    data class PreviewLoaded(val result: ScanResult) : JunkCleanAction
    data object PreviewUnavailable : JunkCleanAction
    data class ToggleItem(val itemId: String) : JunkCleanAction
    data class ToggleCategories(val categories: List<JunkCategory>) : JunkCleanAction
    data object CleaningRequested : JunkCleanAction
    data class DeleteAuthorizationRequested(val count: Int) : JunkCleanAction
    data object DeleteAuthorizationHandled : JunkCleanAction
    data class CleaningCompleted(val summary: CleanupSummary) : JunkCleanAction
    data class Failed(@StringRes val messageRes: Int, val message: String?, val duringScan: Boolean = false) : JunkCleanAction
    data object ResultCleared : JunkCleanAction
    data object ResultAnimationCompleted : JunkCleanAction
    data object ActiveOperationCancelled : JunkCleanAction
    data object CleaningCancelled : JunkCleanAction
    data class StopDialogShown(val phase: JunkCleanPhase) : JunkCleanAction
    data object StopDialogDismissed : JunkCleanAction
}

sealed interface JunkCleanIntent {
    data object Entered : JunkCleanIntent
    data object ScanRequested : JunkCleanIntent
    data object Back : JunkCleanIntent
    data object CleanSelected : JunkCleanIntent
    data class ToggleItem(val itemId: String) : JunkCleanIntent
    data class ToggleCategories(val categories: List<JunkCategory>) : JunkCleanIntent
    data object ContinueFromResult : JunkCleanIntent
    data object QuitStoppedOperation : JunkCleanIntent
    data object ResumeStoppedOperation : JunkCleanIntent
    data class DeleteAuthorizationResult(val approved: Boolean) : JunkCleanIntent
    data object CompletionAdFinished : JunkCleanIntent
    data object ExitFinished : JunkCleanIntent
}

enum class JunkExitTarget {
    Back,
    HomeAfterComplete,
}

sealed interface JunkCleanEffect {
    data class Exit(
        val reason: FeatureExitReason,
        val target: JunkExitTarget = JunkExitTarget.Back,
    ) : JunkCleanEffect
    data class RequestDeleteAuthorization(val uris: List<String>) : JunkCleanEffect
    data class Operation(val event: FeatureOperationEvent) : JunkCleanEffect
}
