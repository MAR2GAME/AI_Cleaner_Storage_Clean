package com.aicleanerstorage.spaceclean.app.view.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.settings.SettingsDividerContent
import com.aicleanerstorage.spaceclean.app.view.settings.interaction.SettingsAction
import com.aicleanerstorage.spaceclean.app.view.settings.interaction.SettingsUiState
import com.aicleanerstorage.spaceclean.app.view.settings.interaction.normalizeTemperatureUnit
import com.aicleanerstorage.spaceclean.app.view.settings.ui.part.SettingsNavigationRow
import com.aicleanerstorage.spaceclean.app.view.settings.ui.part.SettingsPanel
import com.aicleanerstorage.spaceclean.app.view.settings.ui.part.SettingsValueRow
import com.aicleanerstorage.spaceclean.base.designsystem.component.SettingsRateDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.TemperatureUnitDialog
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val temperatureUnit = state.temperatureUnit
    var showTemperatureDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }

    SettingsPanel(
        title = stringResource(R.string.nav_settings),
        onBack = { onAction(SettingsAction.BackClicked) },
        cardMinHeight = 312.dp,
        modifier = modifier,
    ) {
        SettingsNavigationRow(
            label = stringResource(R.string.settings_manage_permissions),
            onClick = { onAction(SettingsAction.NavigationRequested(AppDestination.ManagePermissions)) },
        )
        SettingsDividerContent()
        SettingsValueRow(
            label = stringResource(R.string.settings_temperature_unit),
            value = "${'\u00B0'}$temperatureUnit",
            onClick = { showTemperatureDialog = true },
        )
        SettingsDividerContent()
        SettingsNavigationRow(
            label = stringResource(R.string.settings_terms_of_service),
            onClick = { onAction(SettingsAction.TermsClicked) },
        )
        SettingsDividerContent()
        SettingsNavigationRow(
            label = stringResource(R.string.settings_privacy_policy),
            onClick = { onAction(SettingsAction.PrivacyClicked) },
        )
        SettingsDividerContent()
        SettingsNavigationRow(
            label = stringResource(R.string.settings_rate_us),
            onClick = { showRateDialog = true },
        )
        SettingsDividerContent()
        SettingsValueRow(
            label = stringResource(R.string.settings_version),
            value = state.versionLabel,
            onClick = {},
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
            onRate = { onAction(SettingsAction.RateAppClicked) },
        )
    }
}
