package com.aicleanerstorage.spaceclean.feature.settings.view.permissions.ui

import com.aicleanerstorage.spaceclean.feature.settings.view.settings.ui.SettingsFigmaScreen
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.ui.SettingsFigmaToggleRow
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.ui.SettingsFigmaDivider

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation.ManagePermissionsIntent
import com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation.ManagePermissionsUiState

@Composable
fun ManagePermissionsScreen(
    state: ManagePermissionsUiState,
    onAction: (ManagePermissionsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsFigmaScreen(
        title = stringResource(R.string.settings_manage_permissions),
        onBack = { onAction(ManagePermissionsIntent.Back) },
        cardMinHeight = 272.dp,
        modifier = modifier,
    ) {
        state.rows.forEachIndexed { index, row ->
            SettingsFigmaToggleRow(
                label = stringResource(row.labelRes),
                checked = row.checked,
                onClick = { onAction(ManagePermissionsIntent.PermissionClicked(row.item)) },
            )
            if (index < state.rows.lastIndex) {
                SettingsFigmaDivider()
            }
        }
    }
}
