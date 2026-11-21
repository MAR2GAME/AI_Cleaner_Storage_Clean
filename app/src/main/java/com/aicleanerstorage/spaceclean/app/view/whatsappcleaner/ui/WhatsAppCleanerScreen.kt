package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerAction
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerPhase
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerUiState
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part.WhatsAppCleaningContent
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part.WhatsAppErrorContent
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part.WhatsAppLoadingContent
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part.WhatsAppResultContent
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part.WhatsAppScanResultBottomBar
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part.WhatsAppScanResultContent
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteStage
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.base.mvi.asString

@Composable
fun WhatsAppCleanerScreen(
    state: WhatsAppCleanerUiState,
    onAction: (WhatsAppCleanerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showStopDialog by remember { mutableStateOf(false) }

    fun handleBack() {
        when (state.phase) {
            WhatsAppCleanerPhase.Scanning -> {
                onAction(WhatsAppCleanerAction.OperationCancellationRequested)
                showStopDialog = true
            }
            WhatsAppCleanerPhase.Cleaning -> {
                showStopDialog = true
            }
            WhatsAppCleanerPhase.CompleteAnimation,
            WhatsAppCleanerPhase.Result,
            -> onAction(WhatsAppCleanerAction.CompletionExitClicked)
            else -> onAction(WhatsAppCleanerAction.BackClicked)
        }
    }

    BackHandler(onBack = ::handleBack)

    val isCleaningAnimationPhase =
        state.phase == WhatsAppCleanerPhase.Cleaning ||
            state.phase == WhatsAppCleanerPhase.CompleteAnimation

    CleanXScaffoldPage(
        title = stringResource(R.string.whatsapp_cleaner),
        modifier = modifier,
        onBack = ::handleBack,
        contentPadding =
            if (
                state.phase == WhatsAppCleanerPhase.ScanResult ||
                state.phase == WhatsAppCleanerPhase.Result ||
                state.phase == WhatsAppCleanerPhase.Scanning ||
                isCleaningAnimationPhase
            ) {
                PaddingValues(0.dp)
            } else {
                PaddingValues(horizontal = 16.dp, vertical = 16.dp)
            },
        scrollEnabled =
            state.phase != WhatsAppCleanerPhase.Scanning &&
                state.phase != WhatsAppCleanerPhase.Result &&
                !isCleaningAnimationPhase,
        bottomBar = {
            if (state.phase == WhatsAppCleanerPhase.ScanResult) {
                WhatsAppScanResultBottomBar(
                    selectedBytes = state.selectedBytes,
                    onClean = { onAction(WhatsAppCleanerAction.CleanClicked) },
                )
            }
        },
    ) {
        when (state.phase) {
            WhatsAppCleanerPhase.Scanning -> WhatsAppLoadingContent()
            WhatsAppCleanerPhase.Cleaning,
            WhatsAppCleanerPhase.CompleteAnimation,
            ->
                WhatsAppCleaningContent(
                    stage =
                        if (state.phase == WhatsAppCleanerPhase.CompleteAnimation) {
                            CleanXDeleteCompleteStage.Complete
                        } else {
                            CleanXDeleteCompleteStage.Deleting
                        },
                )
            WhatsAppCleanerPhase.ScanResult ->
                WhatsAppScanResultContent(
                    uiState = state,
                    onToggleGroup = { onAction(WhatsAppCleanerAction.GroupToggled(it)) },
                    onToggleCategory = { group, category ->
                        onAction(WhatsAppCleanerAction.CategoryToggled(group, category))
                    },
                    onToggleExpanded = { onAction(WhatsAppCleanerAction.GroupExpansionToggled(it)) },
                )
            WhatsAppCleanerPhase.Result ->
                WhatsAppResultContent(
                    uiState = state,
                    onNavigateTool = { destination -> onAction(WhatsAppCleanerAction.NavigationRequested(destination)) },
                )
            WhatsAppCleanerPhase.Error ->
                WhatsAppErrorContent(
                    message = state.error?.asString() ?: stringResource(R.string.whatsapp_clean_unavailable),
                    onRetry = { onAction(WhatsAppCleanerAction.RetryClicked) },
                )
        }
    }

    if (showStopDialog) {
        StopScanDialog(
            onQuit = {
                showStopDialog = false
                when (state.phase) {
                    WhatsAppCleanerPhase.Cleaning ->
                        onAction(WhatsAppCleanerAction.CleaningCancellationRequested)
                    else -> onAction(WhatsAppCleanerAction.OperationCancellationRequested)
                }
                onAction(WhatsAppCleanerAction.BackClicked)
            },
            onResume = {
                showStopDialog = false
                if (state.phase == WhatsAppCleanerPhase.Scanning) {
                    onAction(WhatsAppCleanerAction.RetryClicked)
                }
            },
        )
    }
}
