package com.aicleanerstorage.spaceclean.app.view.permissions.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.permission.AppPermission

private fun initialRows(): List<ManagePermissionRowState> =
    storageCleanManageItems.map { item ->
        ManagePermissionRowState(
            labelRes = item.labelRes,
            item = item.item,
            checked = false,
        )
    }

class ManagePermissionsViewModel :
    MviViewModel<ManagePermissionsUiState, ManagePermissionsAction, ManagePermissionsEvent>(
        ManagePermissionsUiState(initialRows()),
    ) {
    private var exiting = false

    override fun onAction(intent: ManagePermissionsAction) {
        if (exiting) return
        when (intent) {
            is ManagePermissionsAction.NavigationRequested ->
                sendEvent(ManagePermissionsEvent.Navigate(intent.destination))
            ManagePermissionsAction.RefreshRequested -> Unit
            ManagePermissionsAction.BackClicked -> {
                exiting = true
                sendEvent(ManagePermissionsEvent.NavigateBack)
            }
            is ManagePermissionsAction.PermissionClicked ->
                sendEvent(ManagePermissionsEvent.OpenSettings(intent.item))
            is ManagePermissionsAction.StatusesChanged ->
                setState { ManagePermissionsReducer.reduce(this, intent) }
        }
    }
}

private data class StorageCleanPermissionManageItem(
    val item: AppPermission,
    val labelRes: Int,
)

private val storageCleanManageItems: List<StorageCleanPermissionManageItem> =
    listOf(
        StorageCleanPermissionManageItem(AppPermission.StorageFiles, R.string.settings_storage_permission),
        StorageCleanPermissionManageItem(AppPermission.UsageAccess, R.string.settings_usage_data_permission),
        StorageCleanPermissionManageItem(AppPermission.Location, R.string.settings_location_permission),
        StorageCleanPermissionManageItem(
            AppPermission.NotificationListener,
            R.string.settings_notification_permission,
        ),
    )
