package com.aicleanerstorage.spaceclean.feature.applock.view.applock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockPage
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockIntent
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockUiState

@Composable
fun AppLockContentView(
    uiState: AppLockUiState,
    onAction: (AppLockIntent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppLockBackground)
    ) {
        when (uiState.page) {
            AppLockPage.SelectApps -> AppLockSelectAppsView(
                uiState = uiState,
                onTogglePackage = { onAction(AppLockIntent.TogglePackage(it)) }
            )
            AppLockPage.Pin -> Unit
            AppLockPage.Manage -> AppLockManageView(
                uiState = uiState,
                onOpenSearch = { onAction(AppLockIntent.OpenSearch) },
                onTogglePackage = { onAction(AppLockIntent.TogglePackage(it)) },
                onToggleAll = { onAction(AppLockIntent.ToggleAllApps) },
                onAutoLockChange = { onAction(AppLockIntent.AutoLockChanged(it)) }
            )
            AppLockPage.Search -> AppLockSearchView(
                uiState = uiState,
                onSearch = { onAction(AppLockIntent.SearchQueryChanged(it)) },
                onTogglePackage = { onAction(AppLockIntent.TogglePackage(it)) }
            )
            AppLockPage.Settings -> AppLockSettingsView(
                uiState = uiState,
                onMonitoringChange = { onAction(AppLockIntent.MonitoringChanged(it)) },
                onVibrationChange = { onAction(AppLockIntent.VibrationChanged(it)) },
                onStartChangePin = { onAction(AppLockIntent.StartChangePin) }
            )
        }
    }
}
