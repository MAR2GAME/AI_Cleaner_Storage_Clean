package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.error.JunkError
import com.aicleanerstorage.spaceclean.domain.model.junk.CleanupSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkMemoryCleanModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanResultModel

sealed interface JunkCleanPhase {
    data object Idle : JunkCleanPhase

    data class CheckingPermission(
        val request: JunkOperation,
    ) : JunkCleanPhase

    data object Scanning : JunkCleanPhase

    data object Preview : JunkCleanPhase

    data object Cleaning : JunkCleanPhase

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

data class JunkCleanExecution(
    val directCleanedFiles: List<JunkFileModel> = emptyList(),
    val directFailedFiles: List<JunkFileModel> = emptyList(),
    val directFreedSpace: Long = 0L,
    val memoryResult: JunkMemoryCleanModel? = null,
)

data class JunkCleanSession(
    val lastScan: JunkScanResultModel? = null,
    val pendingCleanFiles: List<JunkFileModel> = emptyList(),
    val execution: JunkCleanExecution = JunkCleanExecution(),
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
    val session: JunkCleanSession = JunkCleanSession(),
    val error: UiText? = null,
    val showStopDialog: Boolean = false,
    val stopDialogPhase: JunkCleanPhase? = null,
    val interactionsEnabled: Boolean = true,
)

sealed interface JunkCleanAction {
    data object Entered : JunkCleanAction

    data object ScanRequested : JunkCleanAction

    data object BackClicked : JunkCleanAction

    data object CleanClicked : JunkCleanAction

    data class ItemToggled(
        val itemId: String,
    ) : JunkCleanAction

    data class CategoriesToggled(
        val categories: List<JunkCategory>,
    ) : JunkCleanAction

    data object ContinueClicked : JunkCleanAction

    data object StopConfirmed : JunkCleanAction

    data object StopDismissed : JunkCleanAction

    data class PermissionResultReceived(
        val request: JunkOperation,
        val granted: Boolean,
    ) : JunkCleanAction
}

/** ViewModel 内部对状态机的更新，不暴露给 UI。 */
internal sealed interface JunkCleanUpdate

internal data class JunkPermissionCheckStarted(
    val request: JunkOperation,
) : JunkCleanUpdate

internal data class JunkPermissionCheckRestored(
    val request: JunkOperation,
    val previous: JunkCleanUiState,
) : JunkCleanUpdate

internal data class JunkPermissionDenied(
    val request: JunkOperation,
) : JunkCleanUpdate

internal data class JunkPermissionCheckDeferred(
    val request: JunkOperation,
) : JunkCleanUpdate

internal data object JunkScanStarted : JunkCleanUpdate

internal data class JunkScanProgressUpdated(
    val progress: Float,
    val currentCategory: JunkCategory?,
    val foundItemCount: Int,
    val foundTotalSize: Long,
) : JunkCleanUpdate

internal data class JunkScanCompleted(
    val result: JunkScanResultModel,
) : JunkCleanUpdate

internal data class JunkPreviewLoaded(
    val result: JunkScanResultModel,
) : JunkCleanUpdate

internal data object JunkPreviewLoadFailed : JunkCleanUpdate

internal data object JunkCleaningRequested : JunkCleanUpdate

internal data class JunkCleanFilesSelected(
    val files: List<JunkFileModel>,
) : JunkCleanUpdate

internal data class JunkExecutionUpdated(
    val execution: JunkCleanExecution,
) : JunkCleanUpdate

internal data class JunkCleaningCompleted(
    val summary: CleanupSummaryModel,
    val remaining: JunkScanResultModel? = null,
) : JunkCleanUpdate

internal data class JunkFailed(
    val error: JunkError,
    val duringScan: Boolean = false,
) : JunkCleanUpdate

internal data object JunkResultCleared : JunkCleanUpdate

internal data object JunkResultAnimationCompleted : JunkCleanUpdate

internal data object JunkActiveOperationCancelled : JunkCleanUpdate

internal data class JunkCleaningStopped(
    val remaining: JunkScanResultModel,
    val selected: Set<JunkFileModel>,
    val summary: CleanupSummaryModel,
) : JunkCleanUpdate

internal data class JunkStopDialogShown(
    val phase: JunkCleanPhase,
) : JunkCleanUpdate

internal data object JunkStopDialogDismissed : JunkCleanUpdate

sealed interface JunkCleanEvent {
    /** 页面该关闭了；回退还是清栈由宿主决定，ViewModel 不描述导航策略。 */
    data object Exit : JunkCleanEvent
}
