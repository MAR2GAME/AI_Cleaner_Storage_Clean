package com.aicleanerstorage.spaceclean.app.view.battery.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryInfoAction
import com.aicleanerstorage.spaceclean.app.view.battery.ui.part.BatteryElectricCurrentCard
import com.aicleanerstorage.spaceclean.app.view.battery.ui.part.BatteryInformationCard
import com.aicleanerstorage.spaceclean.app.view.battery.ui.part.BatteryTemperatureCard
import com.aicleanerstorage.spaceclean.app.view.battery.ui.part.BatteryTopCard
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.base.designsystem.component.ToolboxScanningContent
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding

@Composable
fun BatteryInfoScreen(
    state: DeviceInfoUiState,
    onAction: (BatteryInfoAction) -> Unit,
) {
    CleanXScaffoldPage(
        title = stringResource(R.string.battery_info),
        backgroundBrush =
            Brush.linearGradient(
                colors = listOf(BatteryPageBackground, BatteryPageBackground),
            ),
        onBack = { onAction(BatteryInfoAction.BackClicked) },
        contentPadding = if (state.isScanning) PaddingValues(0.dp) else PaddingValues(horizontal = 16.dp),
        scrollEnabled = !state.isScanning,
        verticalArrangement = Arrangement.Top,
    ) {
        if (state.isScanning) {
            ToolboxScanningContent(
                centerIconRes = R.drawable.ic_scan_battery_info,
                captionText = stringResource(R.string.battery_info),
            )
        } else {
            BatteryTopCard(
                statusText = state.battery.statusText,
                capacityText = state.batteryCapacityText,
            )
            Spacer(modifier = Modifier.height(16.dp))
            BatteryInformationCard(state)
            Spacer(modifier = Modifier.height(20.dp))
            BatteryTemperatureCard(state)
            Spacer(modifier = Modifier.height(20.dp))
            BatteryElectricCurrentCard(
                uiState = state,
                onRangeSelected = { onAction(BatteryInfoAction.RangeSelected(it)) },
            )
            Spacer(modifier = Modifier
                .stableNavigationBarsPadding()
                .height(24.dp))
        }
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(BatteryInfoAction.QuitScanConfirmed) },
            onResume = { onAction(BatteryInfoAction.ResumeScanClicked) },
        )
    }
}

private val BatteryPageBackground = Color(0xFFF4F4F4)
