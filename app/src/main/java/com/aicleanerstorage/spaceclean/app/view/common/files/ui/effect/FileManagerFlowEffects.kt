package com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import kotlinx.coroutines.flow.Flow

@Composable
fun FileManagerFlowEffects(
    effects: Flow<FileManagerEvent>,
    navigator: AppNavigator,
    permissionState: FileManagerPermissionState,
    error: UiText?,
    onClearError: () -> Unit,
    onStartIfNeeded: () -> Unit,
) {
    FileManagerOperationEventsEffect(effects, permissionState, navigator)
    FileManagerErrorToastEffect(error, onClearError)
    FileManagerStartEffect(permissionState, onStartIfNeeded)
}
