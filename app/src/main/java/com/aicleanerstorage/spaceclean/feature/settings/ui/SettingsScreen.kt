package com.aicleanerstorage.spaceclean.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.settings.SettingsAction
import com.aicleanerstorage.spaceclean.feature.settings.SettingsUiState
import com.aicleanerstorage.spaceclean.feature.settings.normalizeTemperatureUnit

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val temperatureUnit = state.temperatureUnit
    var showTemperatureDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }

    SettingsFigmaScreen(
        title = stringResource(R.string.nav_settings),
        onBack = { onAction(SettingsAction.Back) },
        cardMinHeight = 312.dp,
        modifier = modifier,
    ) {
        SettingsFigmaNavigationRow(
            label = stringResource(R.string.settings_manage_permissions),
            onClick = { onNavigate(AppDestination.ManagePermissions) },
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
            onClick = { onAction(SettingsAction.OpenTerms) },
        )
        SettingsFigmaDivider()
        SettingsFigmaNavigationRow(
            label = stringResource(R.string.settings_privacy_policy),
            onClick = { onAction(SettingsAction.OpenPrivacy) },
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
                onAction(SettingsAction.TemperatureUnitChanged(normalized))
                showTemperatureDialog = false
            },
        )
    }

    if (showRateDialog) {
        SettingsRateDialog(
            onDismiss = { showRateDialog = false },
            onRate = { onAction(SettingsAction.RateApp) },
        )
    }
}
