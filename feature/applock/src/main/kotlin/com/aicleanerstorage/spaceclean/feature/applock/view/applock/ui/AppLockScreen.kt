package com.aicleanerstorage.spaceclean.feature.applock.view.applock.ui

import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockPage
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockIntent
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockUiState
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.ui.components.CleanXIconButtonSize
import com.aicleanerstorage.spaceclean.core.ui.components.CleanXPillShape
import com.aicleanerstorage.spaceclean.core.ui.components.CleanXScaffoldPage

@Composable
fun AppLockScreen(
    state: AppLockUiState,
    onAction: (AppLockIntent) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onAction(AppLockIntent.Back) }

    if (state.page == AppLockPage.Pin) {
        AppLockPinView(
            uiState = state,
            onDigit = { onAction(AppLockIntent.AddPinDigit(it)) },
            onDelete = { onAction(AppLockIntent.RemovePinDigit) },
            onBack = { onAction(AppLockIntent.Back) },
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
        onBack = { onAction(AppLockIntent.Back) },
        actions = {
            if (state.page == AppLockPage.Manage) {
                Box(
                    modifier = Modifier
                        .size(CleanXIconButtonSize)
                        .clip(CleanXPillShape)
                        .clickable { onAction(AppLockIntent.OpenSettings) },
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
                    onClick = { onAction(AppLockIntent.BeginCreatePin) },
                    enabled = state.hasSelectedApps
                )
            }
        }
    ) {
        AppLockContentView(
            uiState = state,
            onAction = onAction,
        )
    }
}
