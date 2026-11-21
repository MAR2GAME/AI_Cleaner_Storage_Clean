package com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui

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
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoAction
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part.BatteryCard
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part.CpuCard
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part.DeviceModelCard
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part.ScreenCard
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part.SensorsCard
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part.StateInfoCard
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.base.designsystem.component.ToolboxScanningContent
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding

@Composable
fun DeviceInfoScreen(
    state: DeviceInfoUiState,
    onAction: (DeviceInfoAction) -> Unit,
) {
    CleanXScaffoldPage(
        title = stringResource(R.string.device_model),
        backgroundBrush =
            Brush.linearGradient(
                colors = listOf(DevicePageBackground, DevicePageBackground),
            ),
        onBack = { onAction(DeviceInfoAction.BackClicked) },
        contentPadding = if (state.isScanning) PaddingValues(0.dp) else PaddingValues(horizontal = 16.dp),
        scrollEnabled = !state.isScanning,
        verticalArrangement = Arrangement.Top,
    ) {
        if (state.isScanning) {
            ToolboxScanningContent(
                centerIconRes = R.drawable.ic_scan_device_info,
                captionText = stringResource(R.string.onboarding_checking_device_info),
            )
        } else {
            DeviceModelCard(state.hardware)
            Spacer(modifier = Modifier.height(16.dp))
            StateInfoCard(state)
            Spacer(modifier = Modifier.height(20.dp))
            ScreenCard(state.hardware)
            Spacer(modifier = Modifier.height(20.dp))
            BatteryCard(state)
            Spacer(modifier = Modifier.height(20.dp))
            SensorsCard(state.hardware)
            Spacer(modifier = Modifier.height(20.dp))
            CpuCard(state.hardware)
            Spacer(modifier = Modifier.stableNavigationBarsPadding().height(24.dp))
        }
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(DeviceInfoAction.QuitScanConfirmed) },
            onResume = { onAction(DeviceInfoAction.ResumeScanClicked) },
        )
    }
}

private val DevicePageBackground = Color(0xFFF4F4F4)
