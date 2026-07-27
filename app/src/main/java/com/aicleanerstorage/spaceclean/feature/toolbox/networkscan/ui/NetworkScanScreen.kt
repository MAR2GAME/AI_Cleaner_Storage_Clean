package com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.ui


import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanUiState

import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanPhase

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.ui.components.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.common.ui.components.buttons.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXButtonHorizontalPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXButtonShape
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXButtonVerticalPadding
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.ui.NetworkScanContentView

@Composable
fun NetworkScanScreen(
    uiState: NetworkScanUiState,
    onBack: () -> Unit,
    onRefreshWifi: () -> Unit,
    onSwitchWifi: () -> Unit,
    onScan: () -> Unit,
    onOpenDevices: () -> Unit,
) {
    CleanXScaffoldPage(
        title = stringResource(R.string.network_scan),
        onBack = onBack,
        backgroundBrush = Brush.linearGradient(
            colors = listOf(NetworkScanPageBg, NetworkScanPageBg),
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        bottomBar = {
            NetworkScanBottomBar(
                uiState = uiState,
                onRefreshWifi = onRefreshWifi,
                onSwitchWifi = onSwitchWifi,
                onScan = onScan,
            )
        },
    ) {
        NetworkScanContentView(
            uiState = uiState,
            onDevicesClick = onOpenDevices,
        )
    }
}
@Composable
private fun NetworkScanBottomBar(
    uiState: NetworkScanUiState,
    onRefreshWifi: () -> Unit,
    onSwitchWifi: () -> Unit,
    onScan: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .stableNavigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        when {
            !uiState.hasWifi -> {
                CleanXPrimaryButton(
                    text = stringResource(R.string.scan_again),
                    onClick = onRefreshWifi,
                )
            }
            uiState.phase == NetworkScanPhase.Result -> {
                CleanXPrimaryButton(
                    text = stringResource(R.string.switch_wifi),
                    onClick = onSwitchWifi,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onScan,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                    shape = RoundedCornerShape(40.dp),
                    border = BorderStroke(1.56.dp, NetworkScanButtonOutline),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = NetworkScanButtonOutline,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.scan_again),
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
            else -> {
                CleanXPrimaryButton(
                    text = stringResource(R.string.scan_wifi),
                    onClick = onScan,
                    enabled = uiState.phase != NetworkScanPhase.Scanning,
                )
            }
        }
    }
}

@Composable
private fun NetworkScanPrimaryButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(
                    brush =
                        Brush.horizontalGradient(
                            colors =
                                listOf(
                                    Color(0xFFD0FD91),
                                    Color(0xFF5DD595),
                                ),
                        ),
                    shape = CleanXButtonShape,
                ),
        shape = CleanXButtonShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = NetworkScanNavy,
            ),
        contentPadding =
            PaddingValues(
                horizontal = CleanXButtonHorizontalPadding,
                vertical = CleanXButtonVerticalPadding,
            ),
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
