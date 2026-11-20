package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import kotlinx.coroutines.flow.Flow

@Composable
fun FileManagerOperationEventsEffect(
    effects: Flow<FileManagerEvent>,
    permissionState: FileManagerPermissionState,
    navigator: AppNavigator,
) {
    val currentNavigator by rememberUpdatedState(navigator)
    LaunchedEffect(effects) {
        effects.collect { effect ->
            if (permissionState.leavingPage) return@collect
            when (effect) {
                is FileManagerEvent.Exit ->
                    if (effect.home) permissionState.leaveHome(currentNavigator)
                    else permissionState.leaveBack(currentNavigator)
                is FileManagerEvent.Navigate -> {
                    if (effect.reset) {
                        permissionState.markLeaving()
                        currentNavigator.resetToRoot(effect.destination)
                    } else {
                        currentNavigator.navigate(effect.destination)
                    }
                }
                FileManagerEvent.NavigateBack -> currentNavigator.back()
            }
        }
    }
}

class FileManagerPermissionState(val granted: Boolean) {
    var leavingPage by mutableStateOf(false)
        private set

    fun markLeaving() {
        leavingPage = true
    }
}

fun FileManagerPermissionState.leaveHome(router: AppNavigator) {
    markLeaving()
    router.resetToRoot(AppDestination.Main())
}

fun FileManagerPermissionState.leaveBack(router: AppNavigator) {
    if (leavingPage) return
    markLeaving()
    router.back()
}

@Composable
fun FileManagerErrorToastEffect(
    errorMessage: String?,
    onConsumed: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(errorMessage) {
        val message = errorMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        onConsumed()
    }
}

@Composable
fun FileManagerStartEffect(
    permissionState: FileManagerPermissionState,
    onStartIfNeeded: () -> Unit,
) {
    val latestStartIfNeeded by rememberUpdatedState(onStartIfNeeded)
    LaunchedEffect(permissionState.leavingPage) {
        if (permissionState.leavingPage) return@LaunchedEffect
        latestStartIfNeeded()
    }
}

@Composable
fun FileManagerStopOperationDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    onQuit: () -> Unit,
    onResume: () -> Unit,
) {
    if (permissionGranted && visible) {
        StopScanDialog(
            onQuit = onQuit,
            onResume = onResume,
        )
    }
}

@Composable
fun FileManagerDeleteConfirmDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    ownerState: FileManagerPermissionState,
    onCancel: () -> Unit,
    onDeleteReady: () -> Unit,
) {
    if (permissionGranted && visible) {
        DeleteConfirmDialog(
            onCancel = onCancel,
            onDelete = { if (!ownerState.leavingPage) onDeleteReady() },
        )
    }
}

@Composable
fun FileManagerNoResultsDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    onBack: () -> Unit,
) {
    if (permissionGranted && visible) {
        NoResultsDialog(onBack = onBack)
    }
}
