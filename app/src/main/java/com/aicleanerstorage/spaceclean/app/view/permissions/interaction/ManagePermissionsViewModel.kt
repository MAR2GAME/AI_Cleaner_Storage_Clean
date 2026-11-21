package com.aicleanerstorage.spaceclean.app.view.permissions.interaction

import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel

class ManagePermissionsViewModel :
    MviViewModel<ManagePermissionsUiState, ManagePermissionsAction, ManagePermissionsEvent>(
        ManagePermissionsUiState(),
    ) {
    private var exiting = false

    override fun onAction(intent: ManagePermissionsAction) {
        if (exiting) return
        when (intent) {
            ManagePermissionsAction.BackClicked -> {
                exiting = true
                sendEvent(ManagePermissionsEvent.NavigateBack)
            }

            is ManagePermissionsAction.PermissionClicked ->
                sendEvent(ManagePermissionsEvent.OpenPermissionSettings(intent.permission))

            is ManagePermissionsAction.StatusesChanged ->
                setState { ManagePermissionsReducer.reduce(it, intent) }
        }
    }
}
