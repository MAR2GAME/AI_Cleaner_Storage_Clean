package com.aicleanerstorage.spaceclean.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.settings.ManagePermissionsAction
import com.aicleanerstorage.spaceclean.feature.settings.ManagePermissionsUiState

@Composable
fun ManagePermissionsScreen(
    state: ManagePermissionsUiState,
    onAction: (ManagePermissionsAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsFigmaScreen(
        title = stringResource(R.string.settings_manage_permissions),
        onBack = { onAction(ManagePermissionsAction.Back) },
        cardMinHeight = 272.dp,
        modifier = modifier,
    ) {
        state.rows.forEachIndexed { index, row ->
            SettingsFigmaToggleRow(
                label = stringResource(row.labelRes),
                checked = row.checked,
                onClick = { onAction(ManagePermissionsAction.PermissionClicked(row.item)) },
            )
            if (index < state.rows.lastIndex) {
                SettingsFigmaDivider()
            }
        }
    }
}
