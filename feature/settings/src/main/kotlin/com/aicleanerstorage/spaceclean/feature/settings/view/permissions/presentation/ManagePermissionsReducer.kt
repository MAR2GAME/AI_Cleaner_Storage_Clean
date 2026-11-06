package com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation



internal object ManagePermissionsReducer {
    fun reduce(state: ManagePermissionsUiState, intent: ManagePermissionsIntent): ManagePermissionsUiState =
        if (intent is ManagePermissionsIntent.StatusesChanged) state.copy(
            rows = state.rows.map { it.copy(checked = intent.statuses[it.item] == true) },
        ) else state
}
