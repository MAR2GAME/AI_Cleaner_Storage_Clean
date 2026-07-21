package com.aicleanerstorage.spaceclean.feature.applock.ui

import com.aicleanerstorage.spaceclean.feature.applock.AppLockPage
import com.aicleanerstorage.spaceclean.feature.applock.AppLockPinStep
import com.aicleanerstorage.spaceclean.feature.applock.AppLockAction
import com.aicleanerstorage.spaceclean.feature.applock.AppLockUiState
import com.aicleanerstorage.spaceclean.feature.applock.AppLockViewModel
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.runtime.featureflow.FeatureExitReason
import com.aicleanerstorage.spaceclean.app.navigation.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXIconButtonSize
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXPillShape
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.permission.ProtectedAction

@Composable
internal fun AppLockScreen(
    state: AppLockUiState,
    onAction: (AppLockAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler { onAction(AppLockAction.Back) }

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
        onBack = { onAction(AppLockAction.Back) },
        actions = {
            if (state.page == AppLockPage.Manage) {
                Box(
                    modifier = Modifier
                        .size(CleanXIconButtonSize)
                        .clip(CleanXPillShape)
                        .clickable { onAction(AppLockAction.OpenSettings) },
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
                    onClick = { onAction(AppLockAction.BeginCreatePin) },
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
