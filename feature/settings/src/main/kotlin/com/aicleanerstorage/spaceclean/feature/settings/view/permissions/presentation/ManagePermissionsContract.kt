package com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType

data class ManagePermissionRowState(
    val labelRes: Int,
    val item: PermissionType,
    val checked: Boolean,
)

data class ManagePermissionsUiState(
    val rows: List<ManagePermissionRowState> = emptyList(),
)

sealed interface ManagePermissionsIntent {
    data class Navigate(val destination: AppDestination) : ManagePermissionsIntent
    data object Back : ManagePermissionsIntent
    data object Refresh : ManagePermissionsIntent
    data class PermissionClicked(val item: PermissionType) : ManagePermissionsIntent
    data class StatusesChanged(val statuses: Map<PermissionType, Boolean>) : ManagePermissionsIntent
}

sealed interface ManagePermissionsEffect {
    data class Navigate(val destination: AppDestination) : ManagePermissionsEffect
 data object Back : ManagePermissionsEffect }
