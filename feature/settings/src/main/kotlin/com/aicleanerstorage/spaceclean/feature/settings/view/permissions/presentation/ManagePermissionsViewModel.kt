package com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.ensureActive
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import com.aicleanerstorage.spaceclean.core.ui.R

private fun initialRows(): List<ManagePermissionRowState> =
    storageCleanManageItems.map { item ->
        ManagePermissionRowState(
            labelRes = item.labelRes,
            item = item.item,
            checked = false,
        )
    }

class ManagePermissionsViewModel(
    private val permissions: com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway,
) : MviViewModel<ManagePermissionsUiState, ManagePermissionsIntent, ManagePermissionsEffect>(
    ManagePermissionsUiState(initialRows()),
) {
    private var permissionJob: kotlinx.coroutines.Job? = null
    private var exiting = false

    override fun onIntent(intent: ManagePermissionsIntent) {
        if (exiting) return
        when (intent) {
            is ManagePermissionsIntent.Navigate -> emitEffect(ManagePermissionsEffect.Navigate(intent.destination))
            ManagePermissionsIntent.Refresh -> refresh()
            ManagePermissionsIntent.Back -> {
                exiting = true
                permissionJob?.cancel()
                emitEffect(ManagePermissionsEffect.Back)
            }
            is ManagePermissionsIntent.PermissionClicked -> {
                if (permissionJob?.isActive == true) return
                permissionJob = viewModelScope.launch {
                    permissions.openSettings(intent.item)
                    ensureActive()
                    refresh()
                }
            }
            is ManagePermissionsIntent.StatusesChanged -> updateState {
                ManagePermissionsReducer.reduce(this, intent)
            }
        }
    }

    private fun refresh() = dispatch(ManagePermissionsIntent.StatusesChanged(
        initialRows().associate { it.item to permissions.isGranted(it.item) },
    ))
}

private data class StorageCleanPermissionManageItem(
    val item: PermissionType,
    val labelRes: Int,
)

private val storageCleanManageItems: List<StorageCleanPermissionManageItem> =
    listOf(
        StorageCleanPermissionManageItem(PermissionType.StorageFiles, R.string.settings_storage_permission),
        StorageCleanPermissionManageItem(PermissionType.UsageAccess, R.string.settings_usage_data_permission),
        StorageCleanPermissionManageItem(PermissionType.Location, R.string.settings_location_permission),
        StorageCleanPermissionManageItem(
            PermissionType.NotificationListener,
            R.string.settings_notification_permission,
        ),
//        StorageCleanPermissionManageItem(PermissionType.Overlay, R.string.settings_overlay_permission),
    )
