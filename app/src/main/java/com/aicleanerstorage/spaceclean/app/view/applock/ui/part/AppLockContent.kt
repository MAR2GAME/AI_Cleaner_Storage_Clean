package com.aicleanerstorage.spaceclean.app.view.applock.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockAction
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockPage
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockUiState
import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockBackground

@Composable
internal fun AppLockContent(
    uiState: AppLockUiState,
    onAction: (AppLockAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppLockBackground)
    ) {
        when (uiState.page) {
            AppLockPage.SelectApps -> AppLockSelectionList(
                uiState = uiState,
                onTogglePackage = { onAction(AppLockAction.PackageToggled(it)) }
            )
            AppLockPage.Pin -> Unit
            AppLockPage.Manage -> AppLockManageContent(
                uiState = uiState,
                onOpenSearch = { onAction(AppLockAction.SearchClicked) },
                onTogglePackage = { onAction(AppLockAction.PackageToggled(it)) },
                onToggleAll = { onAction(AppLockAction.AllAppsSelectionToggled) },
                onAutoLockChange = { onAction(AppLockAction.AutoLockChanged(it)) }
            )
            AppLockPage.Search -> AppLockSearchContent(
                uiState = uiState,
                onSearch = { onAction(AppLockAction.SearchQueryChanged(it)) },
                onTogglePackage = { onAction(AppLockAction.PackageToggled(it)) }
            )
            AppLockPage.Settings -> AppLockSettingsContent(
                uiState = uiState,
                onMonitoringChange = { onAction(AppLockAction.MonitoringChanged(it)) },
                onVibrationChange = { onAction(AppLockAction.VibrationChanged(it)) },
                onStartChangePin = { onAction(AppLockAction.ChangePinClicked) }
            )
        }
    }
}
