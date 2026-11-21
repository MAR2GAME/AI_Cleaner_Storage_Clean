package com.aicleanerstorage.spaceclean.app.view.permissions.interaction

import com.aicleanerstorage.spaceclean.base.permission.AppPermission

data class ManagePermissionRowState(
    val labelRes: Int,
    val item: AppPermission,
    val checked: Boolean,
)

data class ManagePermissionsUiState(
    val rows: List<ManagePermissionRowState> = emptyList(),
)

sealed interface ManagePermissionsAction {
    data object BackClicked : ManagePermissionsAction
    data class PermissionClicked(val item: AppPermission) : ManagePermissionsAction
    data class StatusesChanged(val statuses: Map<AppPermission, Boolean>) : ManagePermissionsAction
}

sealed interface ManagePermissionsEvent {
    data object NavigateBack : ManagePermissionsEvent
    data class OpenSettings(val permission: AppPermission) : ManagePermissionsEvent
}
