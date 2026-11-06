package com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.model.CategoryCleanGroup
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanupSummary
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent

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


internal data class JunkPermissionCheckStarted(val request: JunkPermissionRequest) : JunkCleanIntent
internal data class JunkPermissionCheckRestored(
    val request: JunkPermissionRequest,
    val previous: JunkCleanUiState,
) : JunkCleanIntent
internal data class JunkPermissionDenied(val request: JunkPermissionRequest) : JunkCleanIntent
internal data class JunkPermissionBusy(val request: JunkPermissionRequest) : JunkCleanIntent
internal data object JunkScanStarted : JunkCleanIntent

internal data class JunkScanProgressUpdated(
    val progress: Float,
    val currentCategory: JunkCategory?,
    val foundItemCount: Int,
    val foundTotalSize: Long,
) : JunkCleanIntent

internal data class JunkScanCompleted(val result: ScanResult) : JunkCleanIntent
internal data class JunkPreviewLoaded(val result: ScanResult) : JunkCleanIntent
internal data object JunkPreviewUnavailable : JunkCleanIntent
internal data object JunkCleaningRequested : JunkCleanIntent
internal data class JunkDeleteAuthorizationRequested(val count: Int) : JunkCleanIntent
internal data object JunkDeleteAuthorizationHandled : JunkCleanIntent
internal data class JunkCleaningCompleted(val summary: CleanupSummary) : JunkCleanIntent
internal data class JunkFailed(@StringRes val messageRes: Int, val message: String?, val duringScan: Boolean = false) : JunkCleanIntent
internal data object JunkResultCleared : JunkCleanIntent
internal data object JunkResultAnimationCompleted : JunkCleanIntent
internal data object JunkActiveOperationCancelled : JunkCleanIntent
internal data object JunkCleaningCancelled : JunkCleanIntent
internal data class JunkStopDialogShown(val phase: JunkCleanPhase) : JunkCleanIntent
internal data object JunkStopDialogDismissed : JunkCleanIntent

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
