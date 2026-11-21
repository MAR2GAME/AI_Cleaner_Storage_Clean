package com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveBack
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveHome
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.mvi.resolve
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
                    if (effect.home) {
                        permissionState.leaveHome(currentNavigator)
                    } else {
                        permissionState.leaveBack(currentNavigator)
                    }
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

@Composable
fun FileManagerErrorToastEffect(
    error: UiText?,
    onConsumed: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(error) {
        val message = error?.resolve(context) ?: return@LaunchedEffect
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
