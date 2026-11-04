package com.aicleanerstorage.spaceclean.feature.settings

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.aicleanerstorage.spaceclean.common.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult

import com.aicleanerstorage.spaceclean.feature.settings.*

import com.aicleanerstorage.spaceclean.feature.settings.ManagePermissionsViewModel
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.feature.settings.ui.ManagePermissionsContent
import com.aicleanerstorage.spaceclean.feature.settings.ui.PermissionRowUi
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXContentPadding
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator


import com.aicleanerstorage.spaceclean.feature.settings.ui.ManagePermissionsScreen

@Composable
fun ManagePermissionsRoute(
    navigator: AppNavigator,
    permissionGateway: PermissionGateway,
    viewModel: ManagePermissionsViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    fun refreshStatuses() {
        viewModel.onAction(
            ManagePermissionsAction.StatusesChanged(
                uiState.rows.associate { row -> row.item to permissionGateway.isGranted(row.item) },
            ),
        )
    }

    DisposableEffect(lifecycleOwner, viewModel, permissionGateway) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    refreshStatuses()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel, permissionGateway) {
        refreshStatuses()
    }

    ManagePermissionsScreen(
        state = uiState,
        onAction = actions@{ action ->
            if (exiting) return@actions
            when (action) {
                ManagePermissionsAction.Back -> {
                    exiting = true
                    permissionJob?.cancel()
                    navigator.back()
                }
                ManagePermissionsAction.Refresh -> refreshStatuses()
                is ManagePermissionsAction.PermissionClicked -> {
                    if (permissionJob?.isActive != true) {
                        permissionJob = routeScope.launch {
                            if (permissionGateway.openSettings(action.item) != PermissionResult.Busy) { refreshStatuses() }
                        }
                    }
                }
                is ManagePermissionsAction.StatusesChanged -> viewModel.onAction(action)
            }
        },
        onNavigate = { if (!exiting) navigator.open(it) },
    )
}
