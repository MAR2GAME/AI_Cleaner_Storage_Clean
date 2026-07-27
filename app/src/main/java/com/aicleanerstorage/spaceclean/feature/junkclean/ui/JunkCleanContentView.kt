package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.junkclean.model.CleanItem
import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkCategory
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXBackground
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanPhase
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanUiState
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteStage

@Composable
internal fun JunkCleanContentView(
    uiState: JunkCleanUiState,
    onToggleCategorySelection: (List<JunkCategory>) -> Unit,
    onToggleItem: (CleanItem) -> Unit,
    onNavigateTool: (AppDestination) -> Unit,
    onContinueFromResult: () -> Unit,
) {
    when (uiState.phase) {
        JunkCleanPhase.Scanning -> JunkScanningView(uiState = uiState)
        JunkCleanPhase.Preview ->
            JunkScanResultView(
                groups = uiState.groups,
                checkedEmptyCategories = uiState.checkedEmptyCategories,
                selectedSummary = uiState.selectedSummary,
                onToggleCategorySelection = onToggleCategorySelection,
                onToggleItem = onToggleItem,
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
                AwaitingAuthorizationView(message = uiState.awaitingAuthorizationMessage.orEmpty())
            }
        JunkCleanPhase.Complete ->
            JunkCleanResultView(
                uiState = uiState.cleanResult,
                onNavigateTool = onNavigateTool,
            )
        JunkCleanPhase.Error ->
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
