package com.aicleanerstorage.spaceclean.app.view.permissions.interaction

internal object ManagePermissionsReducer {
    fun reduce(
        state: ManagePermissionsUiState,
        action: ManagePermissionsAction,
    ): ManagePermissionsUiState =
        when (action) {
            is ManagePermissionsAction.StatusesChanged -> state.copy(statuses = action.statuses)
            else -> state
        }
}
