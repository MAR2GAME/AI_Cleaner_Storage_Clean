package com.aicleanerstorage.spaceclean.feature.settings

import com.aicleanerstorage.spaceclean.common.permission.PermissionType
import androidx.lifecycle.ViewModel
import com.aicleanerstorage.spaceclean.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ManagePermissionRowState(
    val labelRes: Int,
    val item: PermissionType,
    val checked: Boolean,
)

data class ManagePermissionsUiState(
    val rows: List<ManagePermissionRowState> = emptyList(),
)

sealed interface ManagePermissionsAction {
    data object Back : ManagePermissionsAction
    data object Refresh : ManagePermissionsAction
    data class PermissionClicked(val item: PermissionType) : ManagePermissionsAction
    data class StatusesChanged(val statuses: Map<PermissionType, Boolean>) : ManagePermissionsAction
}

private fun initialRows(): List<ManagePermissionRowState> =
    storageCleanManageItems.map { item ->
        ManagePermissionRowState(
            labelRes = item.labelRes,
            item = item.item,
            checked = false,
        )
    }

class ManagePermissionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ManagePermissionsUiState(rows = initialRows()))
    val uiState: StateFlow<ManagePermissionsUiState> = _uiState.asStateFlow()

    fun onAction(action: ManagePermissionsAction) {
        when (action) {
            is ManagePermissionsAction.StatusesChanged -> {
                _uiState.value =
                    ManagePermissionsUiState(
                        rows = initialRows().map { row ->
                            row.copy(checked = action.statuses[row.item] == true)
                        },
                    )
            }
            ManagePermissionsAction.Refresh,
            ManagePermissionsAction.Back,
            is ManagePermissionsAction.PermissionClicked -> Unit
        }
    }
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
