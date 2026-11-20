package com.aicleanerstorage.spaceclean.app.view.permissions.interaction



internal object ManagePermissionsReducer {
    fun reduce(state: ManagePermissionsUiState, intent: ManagePermissionsAction): ManagePermissionsUiState =
        if (intent is ManagePermissionsAction.StatusesChanged) state.copy(
            rows = state.rows.map { it.copy(checked = intent.statuses[it.item] == true) },
        ) else state
}
