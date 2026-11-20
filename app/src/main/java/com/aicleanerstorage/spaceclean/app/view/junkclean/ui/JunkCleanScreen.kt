package com.aicleanerstorage.spaceclean.app.view.junkclean.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanAction
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanPhase
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanUiState
import com.aicleanerstorage.spaceclean.app.view.junkclean.ui.part.JunkCleanContent
import com.aicleanerstorage.spaceclean.app.view.junkclean.ui.part.JunkCleanPanel
import com.aicleanerstorage.spaceclean.app.view.junkclean.ui.part.JunkRemoveBar
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXTopAppBar
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.StopScanDialog

@Composable
fun JunkCleanScreen(
    state: JunkCleanUiState,
    resultTools: @Composable (modifier: Modifier, enabled: Boolean) -> Unit,
    onAction: (JunkCleanAction) -> Unit,
    interactionsEnabled: Boolean = true,
) {
    BackHandler {
        if (interactionsEnabled) onAction(JunkCleanAction.BackClicked)
    }

    JunkCleanPanel(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (interactionsEnabled && state.phase == JunkCleanPhase.Preview) {
                JunkRemoveBar(
                    selectedSummary = state.selectedSummary,
                    onClick = { onAction(JunkCleanAction.CleanClicked) },
                )
            }
        },
    ) {
        JunkCleanContent(
            uiState = state,
            interactionsEnabled = interactionsEnabled,
            onToggleCategorySelection = { onAction(JunkCleanAction.CategoriesToggled(it)) },
            onToggleItem = { onAction(JunkCleanAction.ItemToggled(it.junkFile.id)) },
            resultTools = resultTools,
            onContinueFromResult = { onAction(JunkCleanAction.ContinueClicked) },
        )
        CleanXTopAppBar(
            title = stringResource(R.string.junk_removal),
            showBack = true,
            onBack = {
                if (interactionsEnabled) onAction(JunkCleanAction.BackClicked)
            },
        )
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(JunkCleanAction.StopConfirmed) },
            onResume = { onAction(JunkCleanAction.StopDismissed) },
        )
    }
}
