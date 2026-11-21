package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedAction
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedPhase
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedUiState
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedContent
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedStopBar
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.base.designsystem.component.buttons.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding

@Composable
fun NetworkSpeedScreen(
    uiState: NetworkSpeedUiState,
    onAction: (NetworkSpeedAction) -> Unit,
) {
    BackHandler { onAction(NetworkSpeedAction.BackClicked) }
    CleanXScaffoldPage(
        title = stringResource(R.string.network_speed),
        onBack = { onAction(NetworkSpeedAction.BackClicked) },
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
                        NetworkSpeedStopBar(
                            text = stringResource(R.string.stop),
                            onClick = { onAction(NetworkSpeedAction.StopClicked) },
                        )
                    } else {
                        CleanXPrimaryButton(
                            text = stringResource(R.string.run_speed_test),
                            onClick = { onAction(NetworkSpeedAction.SpeedTestRequested) },
                            enabled = uiState.hasNetwork,
                        )
                    }
                }
            }
        },
    ) {
        NetworkSpeedContent(
            uiState = uiState,
            onNavigateTool = { onAction(NetworkSpeedAction.NavigationRequested(it)) },
        )
    }
}
