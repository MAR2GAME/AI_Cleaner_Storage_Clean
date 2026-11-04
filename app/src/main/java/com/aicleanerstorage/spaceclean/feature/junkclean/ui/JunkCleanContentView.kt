package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanResult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.model.CleanItem
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXBackground
import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.JunkCleanPhase
import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.JunkPermissionRequest
import com.aicleanerstorage.spaceclean.feature.junkclean.presentation.JunkCleanUiState
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteStage

@Composable
internal fun JunkCleanContentView(
    uiState: JunkCleanUiState,
    onToggleCategorySelection: (List<JunkCategory>) -> Unit,
    onToggleItem: (CleanItem) -> Unit,
    resultTools: @Composable (modifier: Modifier, enabled: Boolean) -> Unit,
    onContinueFromResult: () -> Unit,
    interactionsEnabled: Boolean = true,
) {
    when (val phase = uiState.phase) {
        JunkCleanPhase.Idle,
        JunkCleanPhase.Scanning -> JunkScanningView(uiState = uiState)
        is JunkCleanPhase.CheckingPermission -> {
            when (phase.request) {
                JunkPermissionRequest.Scan -> JunkScanningView(uiState = uiState)
                JunkPermissionRequest.Clean -> JunkScanResultView(
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
                JunkScanResultView(
                    groups = uiState.groups,
                    checkedEmptyCategories = uiState.checkedEmptyCategories,
                    selectedSummary = uiState.selectedSummary,
                    onToggleCategorySelection = onToggleCategorySelection,
                    onToggleItem = onToggleItem,
                    interactionsEnabled = interactionsEnabled,
                )
        JunkCleanPhase.Cleaning,
        JunkCleanPhase.CompleteAnimation -> JunkCleaningView(
            stage =
                if (uiState.phase == JunkCleanPhase.CompleteAnimation) {
                    CleanXDeleteCompleteStage.Complete
                } else {
                    CleanXDeleteCompleteStage.Deleting
                },
        )
        JunkCleanPhase.AwaitingAuthorization ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(CleanXBackground)
                        .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                AwaitingAuthorizationView(
                    message = stringResource(R.string.result_confirm_system_deletion, uiState.pendingAuthorizationCount),
                )
            }
        JunkCleanPhase.Complete ->
            JunkCleanResultView(
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
                    message = uiState.errorMessage ?: uiState.errorMessageRes?.let { stringResource(it) }.orEmpty(),
                )
            }

    }
}
