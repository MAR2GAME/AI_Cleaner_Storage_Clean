package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.model.CategoryCleanGroup
import com.aicleanerstorage.spaceclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.ScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.CleanupSummaryModel

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


internal data class JunkPermissionCheckStarted(val request: JunkPermissionRequest) : JunkCleanAction
internal data class JunkPermissionCheckRestored(
    val request: JunkPermissionRequest,
    val previous: JunkCleanUiState,
) : JunkCleanAction
internal data class JunkPermissionDenied(val request: JunkPermissionRequest) : JunkCleanAction
internal data class JunkPermissionCheckDeferred(val request: JunkPermissionRequest) : JunkCleanAction
internal data object JunkScanStarted : JunkCleanAction

internal data class JunkScanProgressUpdated(
    val progress: Float,
    val currentCategory: JunkCategory?,
    val foundItemCount: Int,
    val foundTotalSize: Long,
) : JunkCleanAction

internal data class JunkScanCompleted(val result: ScanResultModel) : JunkCleanAction
internal data class JunkPreviewLoaded(val result: ScanResultModel) : JunkCleanAction
internal data object JunkPreviewLoadFailed : JunkCleanAction
internal data object JunkCleaningRequested : JunkCleanAction
internal data class JunkDeleteAuthorizationRequested(val count: Int) : JunkCleanAction
internal data object JunkDeleteAuthorizationHandled : JunkCleanAction
internal data class JunkCleaningCompleted(val summary: CleanupSummaryModel) : JunkCleanAction
internal data class JunkFailed(@StringRes val messageRes: Int, val message: String?, val duringScan: Boolean = false) : JunkCleanAction
internal data object JunkResultCleared : JunkCleanAction
internal data object JunkResultAnimationCompleted : JunkCleanAction
internal data object JunkActiveOperationCancelled : JunkCleanAction
internal data object JunkCleaningCancelled : JunkCleanAction
internal data class JunkStopDialogShown(val phase: JunkCleanPhase) : JunkCleanAction
internal data object JunkStopDialogDismissed : JunkCleanAction

sealed interface JunkCleanAction {
    data object Entered : JunkCleanAction
    data object ScanRequested : JunkCleanAction
    data object BackClicked : JunkCleanAction
    data object CleanClicked : JunkCleanAction
    data class ItemToggled(val itemId: String) : JunkCleanAction
    data class CategoriesToggled(val categories: List<JunkCategory>) : JunkCleanAction
    data object ContinueClicked : JunkCleanAction
    data object StopConfirmed : JunkCleanAction
    data object StopDismissed : JunkCleanAction
    data class DeleteAuthorizationResultReceived(val approved: Boolean) : JunkCleanAction
    data object CompletionAdFinished : JunkCleanAction
}

enum class JunkExitTarget {
    Back,
    HomeAfterComplete,
}

sealed interface JunkCleanEvent {
    data class Exit(
        val target: JunkExitTarget = JunkExitTarget.Back,
    ) : JunkCleanEvent
    data class RequestDeleteAuthorization(val uris: List<String>) : JunkCleanEvent
}
