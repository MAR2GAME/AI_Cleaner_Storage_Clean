package com.aicleanerstorage.spaceclean.app.view.junkclean.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.CleanItem
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanPhase
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanUiState
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkOperation
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXBackground
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteStage
import com.aicleanerstorage.spaceclean.base.mvi.asString
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory

@Composable
internal fun JunkCleanContent(
    uiState: JunkCleanUiState,
    onToggleCategorySelection: (List<JunkCategory>) -> Unit,
    onToggleItem: (CleanItem) -> Unit,
    resultTools: @Composable (modifier: Modifier, enabled: Boolean) -> Unit,
    onContinueFromResult: () -> Unit,
    interactionsEnabled: Boolean = true,
) {
    when (val phase = uiState.phase) {
        JunkCleanPhase.Idle,
        JunkCleanPhase.Scanning,
        -> JunkScanLoading(uiState = uiState)
        is JunkCleanPhase.CheckingPermission -> {
            when (phase.request) {
                JunkOperation.Scan -> JunkScanLoading(uiState = uiState)
                JunkOperation.Clean ->
                    JunkScanResultContent(
                        groups = uiState.groups,
                        checkedEmptyCategories = uiState.checkedEmptyCategories,
                        selectedSummary = uiState.selectedSummary,
                        onToggleCategorySelection = onToggleCategorySelection,
                        onToggleItem = onToggleItem,
                        interactionsEnabled = false,
                    )
            }
        }
        JunkCleanPhase.Preview ->
            JunkScanResultContent(
                stoppedSummary =
                    uiState.stoppedSummary?.let {
                        stringResource(
                            com.aicleanerstorage.spaceclean.R.string.cleanup_stopped_summary,
                            it.cleanedCount,
                            com.aicleanerstorage.spaceclean.base.util.FormatUtil
                                .formatSize(it.freedSpace),
                        )
                    },
                groups = uiState.groups,
                checkedEmptyCategories = uiState.checkedEmptyCategories,
                selectedSummary = uiState.selectedSummary,
                onToggleCategorySelection = onToggleCategorySelection,
                onToggleItem = onToggleItem,
                interactionsEnabled = interactionsEnabled,
            )
        JunkCleanPhase.Cleaning,
        JunkCleanPhase.CompleteAnimation,
        ->
            JunkCleaningContent(
                stage =
                    if (uiState.phase == JunkCleanPhase.CompleteAnimation) {
                        CleanXDeleteCompleteStage.Complete
                    } else {
                        CleanXDeleteCompleteStage.Deleting
                    },
            )
        JunkCleanPhase.Complete ->
            JunkCleanResultContent(
                uiState = uiState.cleanResult,
                resultTools = resultTools,
                interactionsEnabled = interactionsEnabled,
            )
        is JunkCleanPhase.Failed ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(CleanXBackground)
                        .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                JunkCleanErrorView(
                    message = uiState.error?.asString().orEmpty(),
                )
            }
    }
}
