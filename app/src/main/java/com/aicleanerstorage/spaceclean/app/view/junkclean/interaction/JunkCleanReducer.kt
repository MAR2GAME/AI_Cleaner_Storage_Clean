package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import com.aicleanerstorage.spaceclean.domain.junkclean.error.JunkError
import com.aicleanerstorage.spaceclean.domain.junkclean.model.CleanupSummaryModel

val DefaultResultDisplayCategories =
    listOf(
        JunkCategory.CACHE,
        JunkCategory.TEMP_FILE,
        JunkCategory.RESIDUAL,
        JunkCategory.APK,
        JunkCategory.LARGE_FILE,
    )

object JunkCleanReducer {
    fun reduce(
        state: JunkCleanUiState,
        action: JunkCleanAction,
    ): JunkCleanUiState =
        when (action) {
            is JunkPermissionCheckStarted ->
                state.copy(
                    phase = JunkCleanPhase.CheckingPermission(action.request),
                    progress = if (action.request == JunkOperation.Scan) 0f else state.progress,
                    currentCategory = if (action.request == JunkOperation.Scan) null else state.currentCategory,
                    foundItemCount = if (action.request == JunkOperation.Scan) 0 else state.foundItemCount,
                    foundTotalSize = if (action.request == JunkOperation.Scan) 0L else state.foundTotalSize,
                    formattedFoundSize = if (action.request == JunkOperation.Scan) "0 B" else state.formattedFoundSize,
                    error = null,
                )

            is JunkPermissionDenied ->
                state.copy(
                    phase = if (action.request == JunkOperation.Scan) JunkCleanPhase.Idle else JunkCleanPhase.Preview,
                    progress = if (action.request == JunkOperation.Scan) 0f else state.progress,
                    currentCategory = if (action.request == JunkOperation.Scan) null else state.currentCategory,
                    foundItemCount = if (action.request == JunkOperation.Scan) 0 else state.foundItemCount,
                    foundTotalSize = if (action.request == JunkOperation.Scan) 0L else state.foundTotalSize,
                    formattedFoundSize = if (action.request == JunkOperation.Scan) "0 B" else state.formattedFoundSize,
                    error = null,
                    showStopDialog = false,
                    stopDialogPhase = null,
                )

            is JunkPermissionCheckRestored ->
                if (state.phase == JunkCleanPhase.CheckingPermission(action.request)) {
                    action.previous
                } else {
                    state
                }

            is JunkPermissionCheckDeferred -> state

            JunkScanStarted ->
                JunkCleanUiState(
                    phase = JunkCleanPhase.Scanning,
                )

            is JunkScanProgressUpdated ->
                state.takeIf { it.phase == JunkCleanPhase.Scanning }?.copy(
                    progress = action.progress,
                    currentCategory = action.currentCategory,
                    foundItemCount = action.foundItemCount,
                    foundTotalSize = action.foundTotalSize,
                    formattedFoundSize = FormatUtil.formatSize(action.foundTotalSize),
                    error = null,
                ) ?: state

            is JunkScanCompleted ->
                state.copy(
                    progress = 100f,
                    currentCategory = null,
                    foundItemCount = action.result.totalCount,
                    foundTotalSize = action.result.totalSize,
                    formattedFoundSize = FormatUtil.formatSize(action.result.totalSize),
                    error = null,
                )

            is JunkPreviewLoaded -> publishPreview(state, buildPreviewGroups(action.result))

            JunkPreviewLoadFailed ->
                state.copy(
                    phase = JunkCleanPhase.Failed(JunkOperation.Scan),
                    error = JunkError.NoData.toUiText(),
                )

            is JunkCleanAction.ItemToggled -> toggleItem(state, action.itemId)
            is JunkCleanAction.CategoriesToggled -> toggleCategories(state, action.categories)
            JunkCleaningRequested -> requestCleaning(state)

            is JunkDeleteAuthorizationRequested ->
                state.copy(
                    phase = JunkCleanPhase.AwaitingAuthorization,
                    pendingAuthorizationCount = action.count,
                )

            JunkDeleteAuthorizationHandled ->
                state.copy(
                    phase = JunkCleanPhase.Cleaning,
                    pendingAuthorizationCount = 0,
                )

            is JunkCleaningCompleted ->
                state.copy(
                    phase = JunkCleanPhase.CompleteAnimation,
                    cleanResult = action.summary,
                    showStopDialog = false,
                    stopDialogPhase = null,
                    pendingAuthorizationCount = 0,
                    error = null,
                )

            is JunkFailed ->
                state.copy(
                    phase = JunkCleanPhase.Failed(if (action.duringScan) JunkOperation.Scan else JunkOperation.Clean),
                    error = action.error.toUiText(),
                )

            JunkResultCleared ->
                state.copy(
                    phase = JunkCleanPhase.Idle,
                    cleanResult = CleanupSummaryModel(),
                )

            JunkResultAnimationCompleted ->
                if (state.phase == JunkCleanPhase.CompleteAnimation) {
                    state.copy(phase = JunkCleanPhase.Complete)
                } else {
                    state
                }

            JunkActiveOperationCancelled -> state.copy(phase = JunkCleanPhase.Idle)

            is JunkCleaningStopped -> {
                val groups =
                    buildPreviewGroups(action.remaining).map { group ->
                        group.copy(items = group.items.map { it.copy(isChecked = it.junkFile in action.selected) })
                    }
                publishPreview(state, groups).copy(
                    stoppedSummary = action.summary,
                    showStopDialog = false,
                    stopDialogPhase = null,
                )
            }

            JunkCleaningCancelled ->
                if (state.phase == JunkCleanPhase.Cleaning || state.phase == JunkCleanPhase.AwaitingAuthorization) {
                    state.copy(phase = JunkCleanPhase.Preview, pendingAuthorizationCount = 0)
                } else {
                    state
                }

            is JunkStopDialogShown ->
                state.copy(showStopDialog = true, stopDialogPhase = action.phase)

            JunkStopDialogDismissed ->
                state.copy(showStopDialog = false, stopDialogPhase = null)
            else -> state
        }

    private fun toggleItem(
        state: JunkCleanUiState,
        itemId: String,
    ): JunkCleanUiState {
        if (state.phase != JunkCleanPhase.Preview) return state
        val categoryIndex = state.groups.indexOfFirst { group -> group.items.any { it.junkFile.id == itemId } }
        val group = state.groups.getOrNull(categoryIndex) ?: return state
        val groups = state.groups.toMutableList()
        groups[categoryIndex] =
            group.copy(
                items =
                    group.items.map { item ->
                        if (item.junkFile.id == itemId) item.copy(isChecked = !item.isChecked) else item
                    },
            )
        return publishPreview(state, groups, state.checkedEmptyCategories - group.category)
    }

    private fun toggleCategories(
        state: JunkCleanUiState,
        categories: List<JunkCategory>,
    ): JunkCleanUiState {
        if (state.phase != JunkCleanPhase.Preview) return state
        val targetCategories = categories.distinct()
        val targetIndices =
            state.groups.mapIndexedNotNull { index, group ->
                index.takeIf { group.category in targetCategories && group.items.isNotEmpty() }
            }
        if (targetIndices.isEmpty()) {
            val category = targetCategories.firstOrNull() ?: return state
            val checkedEmptyCategories =
                if (category in state.checkedEmptyCategories) {
                    state.checkedEmptyCategories - category
                } else {
                    state.checkedEmptyCategories + category
                }
            return publishPreview(state, state.groups, checkedEmptyCategories)
        }

        val allChecked = targetIndices.flatMap { state.groups[it].items }.all { it.isChecked }
        val groups = state.groups.toMutableList()
        targetIndices.forEach { index ->
            val group = groups[index]
            groups[index] = group.copy(items = group.items.map { it.copy(isChecked = !allChecked) })
        }
        return publishPreview(state, groups, state.checkedEmptyCategories - targetCategories.toSet())
    }

    private fun requestCleaning(state: JunkCleanUiState): JunkCleanUiState {
        if (state.phase != JunkCleanPhase.Preview) return state
        if (state.groups.flatMap { it.items }.any { it.isChecked }) {
            return state.copy(
                phase = JunkCleanPhase.Cleaning,
                pendingAuthorizationCount = 0,
                error = null,
            )
        }
        return state.copy(
            phase = JunkCleanPhase.Failed(JunkOperation.Clean),
            error =
                UiText.Res(
                    if (state.checkedEmptyCategories.isNotEmpty()) {
                        R.string.result_zero_byte_selection_hint
                    } else {
                        R.string.result_select_at_least_one
                    },
                ),
        )
    }

    private fun publishPreview(
        state: JunkCleanUiState,
        groups: List<CategoryCleanGroup>,
        selectedEmptyCategories: Set<JunkCategory> = defaultEmptyCheckedCategories(groups),
    ): JunkCleanUiState {
        val checkedEmptyCategories =
            selectedEmptyCategories
                .filter { category ->
                    groups.firstOrNull { it.category == category }?.items?.isEmpty() ?: true
                }.toSet()
        return state.copy(
            phase = JunkCleanPhase.Preview,
            stoppedSummary = null,
            groups = groups,
            checkedEmptyCategories = checkedEmptyCategories,
            selectedSummary =
                SelectionSummary(
                    checkedCount = groups.sumOf { it.checkedCount },
                    checkedSize = groups.sumOf { it.checkedSize },
                    checkedEmptyCategoryCount = checkedEmptyCategories.size,
                ),
            pendingAuthorizationCount = 0,
            error = null,
        )
    }

    private fun buildPreviewGroups(result: com.aicleanerstorage.cleanerkit.clean.model.ScanResult) =
        result.categoryBreakdown.map { (category, files) ->
            CategoryCleanGroup(category, files.map { CleanItem(junkFile = it, isChecked = it.category == JunkCategory.CACHE) })
        }

    private fun defaultEmptyCheckedCategories(groups: List<CategoryCleanGroup>): Set<JunkCategory> {
        val groupsByCategory = groups.associateBy { it.category }
        val residualHasFiles =
            groupsByCategory[JunkCategory.RESIDUAL]?.items?.isNotEmpty() == true ||
                groupsByCategory[JunkCategory.DUPLICATE]?.items?.isNotEmpty() == true
        return DefaultResultDisplayCategories
            .filter { category ->
                when (category) {
                    JunkCategory.RESIDUAL -> !residualHasFiles
                    else -> groupsByCategory[category]?.items?.isEmpty() ?: true
                }
            }.toSet()
    }
}
