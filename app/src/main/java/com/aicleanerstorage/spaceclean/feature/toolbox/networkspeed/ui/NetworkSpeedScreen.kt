package com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.ui.components.buttons.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.common.ui.components.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedUiState

@Composable
fun NetworkSpeedScreen(
    uiState: NetworkSpeedUiState,
    onBack: () -> Unit,
    onRunSpeedTest: () -> Unit,
    onStopSpeedTest: () -> Unit,
    onNavigateTool: (AppDestination) -> Unit,
) {
    CleanXScaffoldPage(
        title = stringResource(R.string.network_speed),
        onBack = onBack,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        bottomBar = {
            if (uiState.phase != NetworkSpeedPhase.Result && uiState.phase != NetworkSpeedPhase.Completing) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .stableNavigationBarsPadding()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                ) {
                    if (uiState.phase == NetworkSpeedPhase.Testing) {
                        NetworkSpeedStopButton(
                            text = stringResource(R.string.stop),
                            onClick = onStopSpeedTest,
                        )
                    } else {
                        CleanXPrimaryButton(
                            text = stringResource(R.string.run_speed_test),
                            onClick = onRunSpeedTest,
                            enabled = uiState.hasNetwork,
                        )
                    }
                }
            }
        },
    ) {
        NetworkSpeedContentView(
            uiState = uiState,
            onNavigateTool = onNavigateTool,
        )
    }
}

@Composable
private fun NetworkSpeedStopButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = NetworkSpeedStopRed,
                contentColor = Color.White,
            ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
