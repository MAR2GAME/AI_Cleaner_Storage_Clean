package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.ui.components.cleanXDebouncedClick
import com.aicleanerstorage.spaceclean.common.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.common.ui.components.stableStatusBarsPadding
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanUiAction
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanUiState

private val JunkFigmaBackground = Color(0xFFF4F4F4)
private val JunkFigmaText = Color(0xFF282927)

@Composable
fun JunkCleanScreen(
    state: JunkCleanUiState,
    onNavigateTool: (AppDestination) -> Unit,
    onAction: (JunkCleanUiAction) -> Unit,
) {
    BackHandler { onAction(JunkCleanUiAction.Back) }

    JunkCleanScaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        JunkCleanContentView(
            uiState = state,
            onToggleCategorySelection = { onAction(JunkCleanUiAction.ToggleCategories(it)) },
            onToggleItem = { onAction(JunkCleanUiAction.ToggleItem(it.junkFile.id)) },
            onClean = { onAction(JunkCleanUiAction.CleanSelected) },
            onNavigateTool = onNavigateTool,
            onContinueFromResult = { onAction(JunkCleanUiAction.ContinueFromResult) },
        )
        JunkFigmaTopBar(
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
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(JunkFigmaBackground),
    ) {
        content()
    }
}

@Composable
private fun JunkFigmaTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .stableStatusBarsPadding()
                .padding(start = 16.dp, top = 32.dp)
                .height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
            modifier =
                Modifier
                    .size(24.dp)
                    .cleanXDebouncedClick(onClick = onBack),
            tint = JunkFigmaText,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = title,
            color = JunkFigmaText,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}
