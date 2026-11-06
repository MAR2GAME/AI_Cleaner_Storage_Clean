package com.aicleanerstorage.spaceclean.feature.settings.view.settings.ui

import com.aicleanerstorage.spaceclean.core.ui.components.SettingsRateDialog
import com.aicleanerstorage.spaceclean.core.ui.components.TemperatureUnitDialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation.SettingsIntent
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation.SettingsUiState
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation.normalizeTemperatureUnit

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onAction: (SettingsIntent) -> Unit,
    onManagePermissions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val temperatureUnit = state.temperatureUnit
    var showTemperatureDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }

    SettingsFigmaScreen(
        title = stringResource(R.string.nav_settings),
        onBack = { onAction(SettingsIntent.Back) },
        cardMinHeight = 312.dp,
        modifier = modifier,
    ) {
        SettingsFigmaNavigationRow(
            label = stringResource(R.string.settings_manage_permissions),
            onClick = onManagePermissions,
        )
        SettingsFigmaDivider()
        SettingsFigmaValueRow(
            label = stringResource(R.string.settings_temperature_unit),
            value = "${'\u00B0'}$temperatureUnit",
            onClick = { showTemperatureDialog = true },
        )
        SettingsFigmaDivider()
        SettingsFigmaNavigationRow(
            label = stringResource(R.string.settings_terms_of_service),
            onClick = { onAction(SettingsIntent.OpenTerms) },
        )
        SettingsFigmaDivider()
        SettingsFigmaNavigationRow(
            label = stringResource(R.string.settings_privacy_policy),
            onClick = { onAction(SettingsIntent.OpenPrivacy) },
        )
        SettingsFigmaDivider()
        SettingsFigmaNavigationRow(
            label = stringResource(R.string.settings_rate_us),
            onClick = { showRateDialog = true },
        )
    }

    if (showTemperatureDialog) {
        TemperatureUnitDialog(
            selected = temperatureUnit,
            onDismiss = { showTemperatureDialog = false },
            onSelected = { unit ->
                val normalized = unit.normalizeTemperatureUnit()
                onAction(SettingsIntent.TemperatureUnitChanged(normalized))
                showTemperatureDialog = false
            },
        )
    }

    if (showRateDialog) {
        SettingsRateDialog(
            onDismiss = { showRateDialog = false },
            onRate = { onAction(SettingsIntent.RateApp) },
        )
    }
}
