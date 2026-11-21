package com.aicleanerstorage.spaceclean.app.view.applock.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockAction
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockPage
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockUiState
import com.aicleanerstorage.spaceclean.app.view.applock.ui.part.AppLockBottomBar
import com.aicleanerstorage.spaceclean.app.view.applock.ui.part.AppLockContent
import com.aicleanerstorage.spaceclean.app.view.applock.ui.part.AppLockPinPanel
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXIconButtonSize
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXPillShape
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

@Composable
fun AppLockScreen(
    state: AppLockUiState,
    onAction: (AppLockAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onAction(AppLockAction.BackClicked) }

    if (state.page == AppLockPage.Pin) {
        AppLockPinPanel(
            uiState = state,
            onDigit = { onAction(AppLockAction.PinDigitClicked(it)) },
            onDelete = { onAction(AppLockAction.PinBackspaceClicked) },
            onBack = { onAction(AppLockAction.BackClicked) },
        )
        return
    }

    CleanXScaffoldPage(
        title = stringResource(
            when (state.page) {
                AppLockPage.Settings -> R.string.setting
                AppLockPage.Pin -> state.pinStep.titleRes
                else -> R.string.app_lock
            }
        ),
        modifier = modifier,
        scrollEnabled = false,
        contentPadding = PaddingValues(0.dp),
        onBack = { onAction(AppLockAction.BackClicked) },
        actions = {
            if (state.page == AppLockPage.Manage) {
                Box(
                    modifier = Modifier
                        .size(CleanXIconButtonSize)
                        .clip(CleanXPillShape)
                        .clickable { onAction(AppLockAction.SettingsClicked) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.mipmap.ic_setting),
                        contentDescription = stringResource(R.string.setting),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        bottomBar = {
            if (state.page == AppLockPage.SelectApps) {
                AppLockBottomBar(
                    text = stringResource(R.string.lock_selected_apps),
                    onClick = { onAction(AppLockAction.PinCreationRequested) },
                    enabled = state.hasSelectedApps
                )
            }
        }
    ) {
        AppLockContent(
            uiState = state,
            onAction = onAction,
        )
    }
}
