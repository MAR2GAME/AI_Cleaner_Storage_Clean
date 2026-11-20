package com.aicleanerstorage.spaceclean.app.view.permissions.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.settings.SettingsDividerContent
import com.aicleanerstorage.spaceclean.app.view.common.settings.SettingsToggleRow
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsAction
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsUiState
import com.aicleanerstorage.spaceclean.app.view.settings.ui.part.SettingsPanel

@Composable
fun ManagePermissionsScreen(
    state: ManagePermissionsUiState,
    onAction: (ManagePermissionsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsPanel(
        title = stringResource(R.string.settings_manage_permissions),
        onBack = { onAction(ManagePermissionsAction.BackClicked) },
        cardMinHeight = 272.dp,
        modifier = modifier,
    ) {
        state.rows.forEachIndexed { index, row ->
            SettingsToggleRow(
                label = stringResource(row.labelRes),
                checked = row.checked,
                onClick = { onAction(ManagePermissionsAction.PermissionClicked(row.item)) },
            )
            if (index < state.rows.lastIndex) {
                SettingsDividerContent()
            }
        }
    }
}
