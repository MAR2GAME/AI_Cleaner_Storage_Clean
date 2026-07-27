package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXTopAppBar
import com.aicleanerstorage.spaceclean.common.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanUiAction
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanPhase
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanUiState

private val JunkFigmaBackground = Color(0xFFF4F4F4)

@Composable
fun JunkCleanScreen(
    state: JunkCleanUiState,
    onNavigateTool: (AppDestination) -> Unit,
    onAction: (JunkCleanUiAction) -> Unit,
) {
    BackHandler { onAction(JunkCleanUiAction.Back) }

    JunkCleanScaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (state.phase == JunkCleanPhase.Preview) {
                JunkRemoveButton(
                    selectedSummary = state.selectedSummary,
                    onClick = { onAction(JunkCleanUiAction.CleanSelected) },
                )
            }
        },
    ) {
        JunkCleanContentView(
            uiState = state,
            onToggleCategorySelection = { onAction(JunkCleanUiAction.ToggleCategories(it)) },
            onToggleItem = { onAction(JunkCleanUiAction.ToggleItem(it.junkFile.id)) },
            onNavigateTool = onNavigateTool,
            onContinueFromResult = { onAction(JunkCleanUiAction.ContinueFromResult) },
        )
        CleanXTopAppBar(
            title = stringResource(R.string.junk_removal),
            onBack = { onAction(JunkCleanUiAction.Back) },
        )
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(JunkCleanUiAction.QuitStoppedOperation) },
            onResume = { onAction(JunkCleanUiAction.ResumeStoppedOperation) },
        )
    }
}

@Composable
private fun JunkCleanScaffold(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = JunkFigmaBackground,
        bottomBar = bottomBar,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            content()
        }
    }
}
