package com.aicleanerstorage.spaceclean.app.view.permissions.interaction

import com.aicleanerstorage.spaceclean.base.permission.AppPermission

data class ManagePermissionsUiState(
    val statuses: Map<AppPermission, Boolean> = emptyMap(),
)

sealed interface ManagePermissionsAction {
    data object BackClicked : ManagePermissionsAction

    data class PermissionClicked(
        val permission: AppPermission,
    ) : ManagePermissionsAction

    data class StatusesChanged(
        val statuses: Map<AppPermission, Boolean>,
    ) : ManagePermissionsAction
}

sealed interface ManagePermissionsEvent {
    data object NavigateBack : ManagePermissionsEvent

    data class OpenPermissionSettings(
        val permission: AppPermission,
    ) : ManagePermissionsEvent
}
