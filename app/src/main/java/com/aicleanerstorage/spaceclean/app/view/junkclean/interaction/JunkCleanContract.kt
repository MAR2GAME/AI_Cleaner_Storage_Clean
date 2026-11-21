package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.error.JunkError
import com.aicleanerstorage.spaceclean.domain.model.CleanupSummaryModel

sealed interface JunkCleanPhase {
    data object Idle : JunkCleanPhase

    data class CheckingPermission(
        val request: JunkOperation,
    ) : JunkCleanPhase

    data object Scanning : JunkCleanPhase

    data object Preview : JunkCleanPhase

    data object Cleaning : JunkCleanPhase

    data object AwaitingAuthorization : JunkCleanPhase

    data object CompleteAnimation : JunkCleanPhase

    data object Complete : JunkCleanPhase

    data class Failed(
        val operation: JunkOperation,
    ) : JunkCleanPhase
}

enum class JunkOperation {
    Scan,
    Clean,
}

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
    val stoppedSummary: CleanupSummaryModel? = null,
    val cleanResult: CleanupSummaryModel = CleanupSummaryModel(),
    val pendingAuthorizationCount: Int = 0,
    val error: UiText? = null,
    val showStopDialog: Boolean = false,
    val stopDialogPhase: JunkCleanPhase? = null,
    val interactionsEnabled: Boolean = true,
)

sealed interface JunkCleanAction {
    /** Actions the reducer resolves on its own; the ViewModel only forwards them. */
    sealed interface ReducerOnly : JunkCleanAction

    data object Entered : JunkCleanAction

    data object ScanRequested : JunkCleanAction

    data object BackClicked : JunkCleanAction

    data object CleanClicked : JunkCleanAction

    data class ItemToggled(
        val itemId: String,
    ) : ReducerOnly

    data class CategoriesToggled(
        val categories: List<JunkCategory>,
    ) : ReducerOnly

    data object ContinueClicked : JunkCleanAction

    data object StopConfirmed : JunkCleanAction

    data object StopDismissed : JunkCleanAction

    data class PermissionResultReceived(
        val request: JunkOperation,
        val granted: Boolean,
    ) : JunkCleanAction

    data class DeleteAuthorizationResultReceived(
        val deletedIds: Set<String>,
    ) : JunkCleanAction
}

internal data class JunkPermissionCheckStarted(
    val request: JunkOperation,
) : JunkCleanAction.ReducerOnly

internal data class JunkPermissionCheckRestored(
    val request: JunkOperation,
    val previous: JunkCleanUiState,
) : JunkCleanAction.ReducerOnly

internal data class JunkPermissionDenied(
    val request: JunkOperation,
) : JunkCleanAction.ReducerOnly

internal data class JunkPermissionCheckDeferred(
    val request: JunkOperation,
) : JunkCleanAction.ReducerOnly

internal data object JunkScanStarted : JunkCleanAction.ReducerOnly

internal data class JunkScanProgressUpdated(
    val progress: Float,
    val currentCategory: JunkCategory?,
    val foundItemCount: Int,
    val foundTotalSize: Long,
) : JunkCleanAction.ReducerOnly

internal data class JunkScanCompleted(
    val result: ScanResult,
) : JunkCleanAction.ReducerOnly

internal data class JunkPreviewLoaded(
    val result: ScanResult,
) : JunkCleanAction.ReducerOnly

internal data object JunkPreviewLoadFailed : JunkCleanAction.ReducerOnly

internal data object JunkCleaningRequested : JunkCleanAction.ReducerOnly

internal data class JunkDeleteAuthorizationRequested(
    val count: Int,
) : JunkCleanAction.ReducerOnly

internal data object JunkDeleteAuthorizationHandled : JunkCleanAction.ReducerOnly

internal data class JunkCleaningCompleted(
    val summary: CleanupSummaryModel,
) : JunkCleanAction.ReducerOnly

internal data class JunkFailed(
    val error: JunkError,
    val duringScan: Boolean = false,
) : JunkCleanAction.ReducerOnly

internal data object JunkResultCleared : JunkCleanAction.ReducerOnly

internal data object JunkResultAnimationCompleted : JunkCleanAction.ReducerOnly

internal data object JunkActiveOperationCancelled : JunkCleanAction.ReducerOnly

internal data class JunkCleaningStopped(
    val remaining: ScanResult,
    val selected: Set<JunkFile>,
    val summary: CleanupSummaryModel,
) : JunkCleanAction.ReducerOnly

internal data object JunkCleaningCancelled : JunkCleanAction.ReducerOnly

internal data class JunkStopDialogShown(
    val phase: JunkCleanPhase,
) : JunkCleanAction.ReducerOnly

internal data object JunkStopDialogDismissed : JunkCleanAction.ReducerOnly

enum class JunkExitTarget {
    Back,
    HomeAfterComplete,
}

sealed interface JunkCleanEvent {
    data class Exit(
        val target: JunkExitTarget = JunkExitTarget.Back,
    ) : JunkCleanEvent

    data class RequestDeleteAuthorization(
        val ids: List<String>,
    ) : JunkCleanEvent
}
