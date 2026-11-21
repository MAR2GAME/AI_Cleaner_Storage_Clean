package com.aicleanerstorage.spaceclean.app.view.appusage.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageAction
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageUiState
import com.aicleanerstorage.spaceclean.app.view.appusage.ui.part.AppUsageRangeToolbar
import com.aicleanerstorage.spaceclean.app.view.appusage.ui.part.PieChartCard
import com.aicleanerstorage.spaceclean.app.view.appusage.ui.part.UsageDataCard
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.base.designsystem.component.ToolboxScanningContent
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.StopScanDialog

@Composable
fun AppUsageScreen(
    state: AppUsageUiState,
    onAction: (AppUsageAction) -> Unit,
) {
    var rangeExpanded by remember { mutableStateOf(false) }

    CleanXScaffoldPage(
        title = stringResource(R.string.app_usage),
        onBack = { onAction(AppUsageAction.BackClicked) },
        actions = {
            if (state.hasAccess && !state.isScanning) {
                AppUsageRangeToolbar(
                    selectedRange = state.selectedRange,
                    expanded = rangeExpanded,
                    onExpandedChange = { rangeExpanded = it },
                    onRangeSelected = { onAction(AppUsageAction.RangeSelected(it)) },
                )
            }
        },
        backgroundBrush =
            Brush.linearGradient(
                colors = listOf(PageBg, PageBg),
            ),
        contentPadding =
            if (state.hasAccess && state.isScanning) {
                PaddingValues(0.dp)
            } else {
                PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            },
        scrollEnabled = !(state.hasAccess && state.isScanning),
    ) {
        if (state.hasAccess && state.isScanning) {
            ToolboxScanningContent(
                centerIconRes = R.drawable.ic_scan_app_usage,
                captionText = stringResource(R.string.app_usage),
            )
        } else if (state.hasAccess) {
            Spacer(modifier = Modifier.height(12.dp))

            PieChartCard(uiState = state)

            Spacer(modifier = Modifier.height(16.dp))

            UsageDataCard(
                uiState = state,
                onTabSelected = { onAction(AppUsageAction.TabSelected(it)) },
                onStopApp = { onAction(AppUsageAction.StopAppClicked(it)) },
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(AppUsageAction.QuitScanConfirmed) },
            onResume = { onAction(AppUsageAction.ResumeScanClicked) },
        )
    }
}

private val PageBg = Color(0xFFF4F4F4)
